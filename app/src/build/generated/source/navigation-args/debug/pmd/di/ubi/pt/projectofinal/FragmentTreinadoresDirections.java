package pmd.di.ubi.pt.projectofinal;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;

public class FragmentTreinadoresDirections {
  private FragmentTreinadoresDirections() {
  }

  @NonNull
  public static NavDirections actionPersonalsFragmentToPersoanlPerfilFragment() {
    return new ActionOnlyNavDirections(R.id.action_personalsFragment_to_persoanlPerfilFragment);
  }
}
