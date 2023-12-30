package com.usfzy.criminalintent.fragment

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.doOnLayout
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.usfzy.criminalintent.R
import com.usfzy.criminalintent.databinding.FragmentCrimeDetailsBinding
import com.usfzy.criminalintent.model.Crime
import com.usfzy.criminalintent.utils.getScaledBitmap
import com.usfzy.criminalintent.viewmodel.CrimeDetailsViewModel
import com.usfzy.criminalintent.viewmodel.CrimeDetailsViewModelFactory
import kotlinx.coroutines.launch
import java.io.File
import java.util.Date

class CrimeDetailsFragment : Fragment() {

    private var _binding: FragmentCrimeDetailsBinding? = null
    private val args: CrimeDetailsFragmentArgs by navArgs()

    private var photoName: String? = null

    private val crimeDetailsViewModel: CrimeDetailsViewModel by viewModels {
        CrimeDetailsViewModelFactory(args.crimeId)
    }

    private val selectSuspect = registerForActivityResult(
        ActivityResultContracts.PickContact()
    ) { uri: Uri? ->
        uri?.let { parseContactSelection(it) }
    }

    private val takePhoto =
        registerForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { didTakePhoto ->

            if (didTakePhoto && photoName != null) {
                crimeDetailsViewModel.updateCrime { it.copy(photoFileName = photoName) }
            }
        }
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentCrimeDetailsBinding.inflate(inflater, container, false)

        binding.apply {
            crimeTitle.doOnTextChanged { text, _, _, _ ->
                crimeDetailsViewModel.updateCrime { oldCrime ->
                    oldCrime.copy(title = text.toString())
                }
            }
            crimeSolved.setOnCheckedChangeListener { _, isChanged ->
                crimeDetailsViewModel.updateCrime { oldCrime ->
                    oldCrime.copy(isSolved = isChanged)
                }
            }
            crimePhoto.setOnClickListener {
                photoName = "IMG ${Date()}.JPG"
                val photoFile = File(requireContext().applicationContext.filesDir, photoName)

                val photoUri = FileProvider.getUriForFile(
                    requireContext(),
                    "com.usfzy.criminalintent.fileprovider",
                    photoFile,
                )

                takePhoto.launch(photoUri)
            }

//            val captureImageIntent = takePhoto.contract.createIntent(
//                requireContext(), null,
//            )
//            crimePhoto.isEnabled = canResolveIntent(captureImageIntent)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                crimeDetailsViewModel.crime.collect { crime ->
                    crime?.let {
                        updateUi(it)
                    }
                }
            }
        }

        setFragmentResultListener(DatePickerFragment.REQUEST_KEY_DATE) { _, bundle ->
            val date = bundle.getSerializable(DatePickerFragment.BUNDLE_KEY_DATE) as Date
            crimeDetailsViewModel.updateCrime { it.copy(date = date) }
        }
        return binding.root
    }

    private fun updateUi(crime: Crime) {
        binding.apply {
            if (crimeTitle.text.toString() != crime.title) {
                crimeTitle.setText(crime.title)
            }
            crimeDate.text = crime.date.toString()
            crimeDate.setOnClickListener {
                findNavController().navigate(
                    CrimeDetailsFragmentDirections.selectDate(crime.date)
                )
            }
            crimeSolved.isChecked = crime.isSolved
            crimeReport.setOnClickListener {
                val reportIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))
                    putExtra(Intent.EXTRA_TEXT, getCrimeReport(crime))
                }

                val chooserIntent = Intent.createChooser(
                    reportIntent, getString(R.string.send_report)
                )
                startActivity(chooserIntent)
            }

            crimeSuspect.setOnClickListener {
                selectSuspect.launch(null)
            }

            crimeSuspect.text = crime.suspect.ifEmpty {
                getString(R.string.crime_suspect_text)
            }

            updatePhoto(crime.photoFileName)
        }
    }

    private fun getCrimeReport(crime: Crime): String {
        val solvedString = if (crime.isSolved) getString(R.string.crime_report_solved)
        else getString(R.string.crime_report_unsolved)

        val dateString = DateFormat.format(DATE_FORMAT, crime.date).toString()

        val suspectText = if (crime.suspect.isBlank()) getString(R.string.crime_report_no_suspect)
        else getString(R.string.crime_report_suspect, crime.suspect)

        return getString(R.string.crime_report, crime.title, dateString, solvedString, suspectText)
    }

    private fun parseContactSelection(contactUri: Uri) {
        val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)

        val queryCursor = requireActivity().contentResolver
            .query(contactUri, queryFields, null, null, null)

        queryCursor?.use { cursor ->
            if (cursor.moveToFirst()) {
                val suspect = cursor.getString(0)
                crimeDetailsViewModel.updateCrime { oldCrime ->
                    oldCrime.copy(suspect = suspect)
                }
            }
        }
    }

    private fun canResolveIntent(intent: Intent): Boolean {
        val packageManager: PackageManager = requireActivity().packageManager
        intent.addCategory(Intent.CATEGORY_HOME)
        val resolvedActivity: ResolveInfo? =
            packageManager.resolveActivity(
                intent,
                PackageManager.MATCH_DEFAULT_ONLY
            )
        return resolvedActivity != null
    }

    private fun updatePhoto(photoFileName: String?) {
        if (binding.crimePhoto.tag != photoFileName) {
            val photoFile = photoFileName?.let {
                File(requireContext().applicationContext.filesDir, it)
            }
            if (photoFile?.exists() == true) {
                binding.crimePhoto.doOnLayout { measuredView ->
                    val scaledBitmap = getScaledBitmap(
                        photoFile.path,
                        measuredView.width,
                        measuredView.height
                    )
                    binding.crimePhoto.setImageBitmap(scaledBitmap)
                    binding.crimePhoto.tag = photoFileName
                }
            } else {
                binding.crimePhoto.setImageBitmap(null)
                binding.crimePhoto.tag = null
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

    companion object {
        private const val TAG: String = "CrimeDetailsFragment"
        private const val DATE_FORMAT = "EEE, MMM, dd"
    }
}