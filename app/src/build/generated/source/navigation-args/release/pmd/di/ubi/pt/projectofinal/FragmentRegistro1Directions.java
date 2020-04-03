package pmd.di.ubi.pt.projectofinal;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;

public class FragmentRegistro1Directions {
  private FragmentRegistro1Directions() {
  }

  @NonNull
  public static NavDirections actionFragmentRegistro1ToFragmentRegistro2() {
    return new ActionOnlyNavDirections(R.id.action_fragmentRegistro1_to_fragmentRegistro2);
  }
}
