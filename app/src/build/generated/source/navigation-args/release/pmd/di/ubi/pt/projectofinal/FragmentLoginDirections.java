package pmd.di.ubi.pt.projectofinal;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;

public class FragmentLoginDirections {
  private FragmentLoginDirections() {
  }

  @NonNull
  public static NavDirections actionFragmentLoginToFragmentRegistro1() {
    return new ActionOnlyNavDirections(R.id.action_fragmentLogin_to_fragmentRegistro1);
  }
}
