package ru.spbstu.profile.edit_profile.presentation

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ru.spbstu.common.di.FeatureUtils
import ru.spbstu.profile.databinding.FragmentEditProfileBinding
import ru.spbstu.profile.di.ProfileApi
import ru.spbstu.profile.di.ProfileComponent
import javax.inject.Inject
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import com.bumptech.glide.Glide
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModel: EditProfileViewModel

    private val getContent =
        registerForActivityResult(PickPhoto()) { uri: Uri? ->
            if (uri != null) {
                /*Glide.with(this)
                    .load(uri)
                    .centerCrop()
                    .into(binding.frgEditProfileIvAvatar)*/
                binding.frgEditProfileIvAvatarStub.visibility = View.GONE
                viewModel.photoUri = uri
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        inject()
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        binding.frgEditProfileToolbar.setNavigationOnClickListener {
            viewModel.onNavigationIconClick()
        }
        binding.frgEditProfileIvAvatar.setOnClickListener {
            getContent.launch(null)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.uriPhotoState.filterNotNull().collect {
                Glide.with(binding.root)
                    .load(it)
                    .centerCrop()
                    .into(binding.frgEditProfileIvAvatar)
            }
        }
    }

    private fun inject() {
        FeatureUtils.getFeature<ProfileComponent>(this, ProfileApi::class.java)
            .editProfileComponentFactory()
            .create(this)
            .inject(this)
    }

    companion object {
        @JvmStatic
        fun newInstance() = EditProfileFragment()
    }

    private class PickPhoto : ActivityResultContract<Int, Uri?>() {
        override fun createIntent(context: Context, input: Int?): Intent =
            Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            if (resultCode != Activity.RESULT_OK) {
                return null
            }
            return intent?.data
        }

    }
}