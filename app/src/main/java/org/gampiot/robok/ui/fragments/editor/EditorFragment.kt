package org.gampiot.robok.ui.fragments.editor

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.annotation.IdRes 

import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout

import io.github.rosemoe.sora.lang.diagnostic.DiagnosticRegion

import org.gampiot.robok.R
import org.gampiot.robok.databinding.FragmentEditorBinding
import org.gampiot.robok.feature.editor.EditorListener
import org.gampiot.robok.feature.component.terminal.RobokTerminal
import org.gampiot.robok.feature.res.Strings
import org.gampiot.robok.feature.util.base.RobokFragment
import org.gampiot.robok.ui.fragments.build.output.OutputFragment
import org.gampiot.robok.ui.fragments.editor.logs.LogsFragment
import org.gampiot.robok.ui.fragments.editor.diagnostic.DiagnosticFragment
import org.gampiot.robok.ui.fragments.editor.diagnostic.models.DiagnosticItem

import org.robok.compiler.logic.LogicCompiler
import org.robok.compiler.logic.LogicCompilerListener
import org.robok.diagnostic.logic.DiagnosticListener

class EditorFragment(
   private val projectPath: String
) : RobokFragment() {

    var _binding: FragmentEditorBinding? = null
    val binding get() = _binding!!
    val handler = Handler(Looper.getMainLooper())
    val diagnosticTimeoutRunnable = object : Runnable {
        override fun run() {
            binding.diagnosticStatusImage.setBackgroundResource(R.drawable.ic_success_24)
            binding.diagnosticStatusDotProgress.visibility = View.INVISIBLE
            binding.diagnosticStatusImage.visibility = View.VISIBLE
        }
    }
    
    var diagnosticsList: MutableList<DiagnosticItem> = mutableListOf()

    val diagnosticStandTime : Long = 800
    
    var oldCompiler: LogicCompiler = null
    var terminal: RobokTerminal = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        terminal = RobokTerminal(requireContext())
        configureScreen()
    }
    
    fun configureScreen() {
        configureTabLayout()
        configureToolbar()
        configureDrawer()
        configureEditor()
        configureFileTree()
        configureCompiler()
    }
    
    fun configureCompiler() {
        val oldCompilerListener = object : LogicCompilerListener {
            override fun onCompiling(log: String) {
                terminal.addLog(log)
            }

            override fun onCompiled(output: String) {
                val outputFragment = OutputFragment()
                outputFragment.addOutput(requireContext(), layoutInflater, view as ViewGroup, output)

                Snackbar.make(binding.root, Strings.message_compiled, Snackbar.LENGTH_LONG)
                    .setAction(Strings.go_to_outputs) {
                        openFragment(outputFragment)
                        terminal.dismiss()
                    }
                    .show()
            }
        }
        oldCompiler = LogicCompiler(requireContext(), oldCompilerListener)
        configureButtons(oldCompiler)
    }
    
    fun configureButtons (oldCompiler: LogicCompiler) {
        binding.runButton.setOnClickListener {
            val code = binding.codeEditor.text.toString()
            terminal.show()
            oldCompiler.compile(code)
        }

        binding.seeLogs.setOnClickListener {
            terminal.show()
        }
    }

    fun configureTabLayout() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    when (it.text) {
                        getString(Strings.text_logs) -> {
                            openFragment(R.id.drawer_editor_right_fragment_container, LogsFragment())
                        }
                        getString(Strings.text_diagnostic) -> {
                            openFragment(R.id.drawer_editor_right_fragment_container, DiagnosticFragment(diagnosticsList))
                        }
                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
        })
    }

    fun configureToolbar() {
        binding.diagnosticStatusDotProgress.startAnimation()
        binding.toolbar.setNavigationOnClickListener {
              if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
              } else {
                    binding.drawerLayout.openDrawer(GravityCompat.START)
              }
        }
        binding.diagnosticStatusImage.setOnClickListener {
              if (binding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.END)
              } else {
                    binding.drawerLayout.openDrawer(GravityCompat.END)
              }
              binding.tabLayout.getTabAt(1)?.select()
        }
    }

    fun configureDrawer() {
        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            var leftDrawerOffset = 0f
            var rightDrawerOffset = 0f

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                val drawerWidth = drawerView.width
                when (drawerView.id) {
                    R.id.navigation_view_left -> {
                        leftDrawerOffset = drawerWidth * slideOffset
                        binding.content.translationX = leftDrawerOffset
                    }
                    R.id.navigation_view_right -> {
                        rightDrawerOffset = drawerWidth * slideOffset
                        binding.content.translationX = -rightDrawerOffset
                    }
                }
            }

            override fun onDrawerOpened(drawerView: View) {}
            override fun onDrawerClosed(drawerView: View) {
                binding.content.translationX = 0f
                leftDrawerOffset = 0f
                rightDrawerOffset = 0f
            }

            override fun onDrawerStateChanged(newState: Int) {}
        })
    }

    fun configureEditor() {
        val diagnosticListener = object : DiagnosticListener {
            override fun onDiagnosticStatusReceive(isError: Boolean) {
                handler.removeCallbacks(diagnosticTimeoutRunnable)
                
                if (isError) {
                    binding.diagnosticStatusImage.setBackgroundResource(R.drawable.ic_error_24)
                } else {
                    binding.diagnosticStatusImage.setBackgroundResource(R.drawable.ic_success_24)
                }
                binding.diagnosticStatusDotProgress.visibility = View.INVISIBLE
                binding.diagnosticStatusImage.visibility = View.VISIBLE
            }

            override fun onDiagnosticReceive(line: Int, positionStart: Int, positionEnd: Int, msg: String) {
                binding.codeEditor.addDiagnosticInEditor(positionStart, positionEnd, DiagnosticRegion.SEVERITY_ERROR, msg)
                diagnosticsList.add(
                   DiagnosticItem(
                       "Error",
                       msg,
                       1
                   )
                )
                onDiagnosticStatusReceive(true)
            }
        }

        val editorListener = object : EditorListener {
            override fun onEditorTextChange() {
                updateUndoRedo()
                binding.diagnosticStatusDotProgress.visibility = View.VISIBLE
                binding.diagnosticStatusImage.visibility = View.INVISIBLE
                
                handler.removeCallbacks(diagnosticTimeoutRunnable)
                handler.postDelayed(diagnosticTimeoutRunnable, diagnosticStandTime)
            }
        }

        binding.codeEditor.setDiagnosticListener(diagnosticListener)
        binding.codeEditor.setEditorListener(editorListener)
        binding.codeEditor.reload()
        binding.undo.setOnClickListener {
            binding.codeEditor.undo()
            updateUndoRedo()
        }
        binding.redo.setOnClickListener {
            binding.codeEditor.redo()
            updateUndoRedo()
        }
        handler.postDelayed(diagnosticTimeoutRunnable, diagnosticStandTime)
    }
    
    fun configureFileTree() {
        val fileObject = file(projectPath)
        binding.fileTree.loadFiles(fileObject)
    }
    
    fun updateUndoRedo() {
        binding.redo?.let {
            it.isEnabled = binding.codeEditor.isCanRedo()
        }
        binding.undo?.let {
            it.isEnabled = binding.codeEditor.isCanUndo()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
