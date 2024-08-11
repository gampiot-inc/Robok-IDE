package org.gampiot.robok.ui.fragments.project.template.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.transition.MaterialSharedAxis;

import org.gampiot.robok.R;
import org.gampiot.robok.ui.fragments.project.template.model.ProjectTemplate;
import org.gampiot.robok.ui.fragments.project.template.view.ProjectTemplateView;
import org.gampiot.robok.ui.fragments.project.create.CreateProjectFragment;

import java.util.List;

public class ProjectTemplateAdapter extends RecyclerView.Adapter<ProjectTemplateAdapter.ViewHolder> {

    private final List<ProjectTemplate> projectTemplates;
    private final Context context;
    
    public ProjectTemplateAdapter(List<ProjectTemplate> projectTemplates, Context context) {
         this.projectTemplates = projectTemplates;
         this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_project_template, parent, false);
         return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
         ProjectTemplate template = projectTemplates.get(position);
         holder.bind(template);
    }

    @Override
    public int getItemCount() {
         return projectTemplates.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    
         public ProjectTemplate projectTemplate;
         public ProjectTemplateView projectTemplateView;
         
         public ViewHolder(@NonNull View itemView) {
              projectTemplateView = itemView;
              projectTemplateView.setOnClickListener(this);
              super(itemView); 
         }
         
         @Override
         public void onClick(View p1) {
              if(projectTemplate != null){
                   goToCreateProject(projectTemplate);
              }
         }

         public void bind(ProjectTemplate template) {
              projectTemplateView.setProjectTemplate(template);
         }
         
         public void goToCreateProject(ProjectTemplate template) {
              Toast.makeText(context, "d", 4000).show();
              FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
              FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
              fragmentTransaction.replace(R.id.fragment_container, new CreateProjectFragment(MaterialSharedAxis.X, template));
              fragmentTransaction.addToBackStack(null);
              fragmentTransaction.commit();
          }
    }
}
