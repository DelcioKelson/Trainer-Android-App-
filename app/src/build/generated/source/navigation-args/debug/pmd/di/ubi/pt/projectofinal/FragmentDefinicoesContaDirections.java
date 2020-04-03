package pmd.di.ubi.pt.projectofinal;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;

public class FragmentDefinicoesContaDirections {
  private FragmentDefinicoesContaDirections() {
  }

  @NonNull
  public static NavDirections actionDefinicoesContaFragmentToMudarPasswordFragment() {
    return new ActionOnlyNavDirections(R.id.action_definicoesContaFragment_to_mudarPasswordFragment);
  }
}
