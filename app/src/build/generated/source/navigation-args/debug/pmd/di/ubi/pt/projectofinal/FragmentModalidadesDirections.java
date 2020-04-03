package pmd.di.ubi.pt.projectofinal;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;

public class FragmentModalidadesDirections {
  private FragmentModalidadesDirections() {
  }

  @NonNull
  public static NavDirections actionModalidadesFragmentToPersonalsFragment() {
    return new ActionOnlyNavDirections(R.id.action_modalidadesFragment_to_personalsFragment);
  }
}
