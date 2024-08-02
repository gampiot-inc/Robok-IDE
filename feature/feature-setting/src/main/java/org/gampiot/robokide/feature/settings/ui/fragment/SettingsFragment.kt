package org.gampiot.robokide.feature.settings.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.google.android.material.transition.MaterialSharedAxis

import org.gampiot.robokide.feature.settings.R
import org.gampiot.robokide.feature.settings.databinding.FragmentSettingsBinding
import org.gampiot.robokide.feature.base.ui.RobokFragment
import org.gampiot.robokide.feature.settings.ui.fragment.editor.SettingsEditorFragment
import org.gampiot.robokide.feature.res.strings.Strings

import dev.trindadedev.lib.ui.components.preferences.withicon.Preference

class SettingsFragment(private val transitionAxis: Int = MaterialSharedAxis.X) : RobokFragment(transitionAxis) {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureToolbarNavigationBack(binding.toolbar)
        
        val editorSettings = Preference(requireContext())
        editorSettings.setTitle(getString(Strings.settings_editor_title))
        editorSettings.setDescription(getString(Strings.settings_editor_description))
        editorSettings.setIcon(R.drawable.ic_settings_24)
        editorSettings.setPreferenceClickListener {
             openFragment(SettingsEditorFragment(MaterialSharedAxis.X))
        }
        binding.content.addView(editorSettings)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}