package pmd.di.ubi.pt.projectofinal;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;

public class FragmentPersoanlPerfilDirections {
  private FragmentPersoanlPerfilDirections() {
  }

  @NonNull
  public static NavDirections actionPersoanlPerfilFragmentToGerarMarcacaoFragment() {
    return new ActionOnlyNavDirections(R.id.action_persoanlPerfilFragment_to_gerarMarcacaoFragment);
  }
}
