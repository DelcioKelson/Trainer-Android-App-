package pmd.di.ubi.pt.projectofinal;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;

public class FragmentTodasMarcacoesDirections {
  private FragmentTodasMarcacoesDirections() {
  }

  @NonNull
  public static NavDirections actionFragmentTodasMarcacoesToMarcacoesFragment() {
    return new ActionOnlyNavDirections(R.id.action_fragmentTodasMarcacoes_to_marcacoesFragment);
  }

  @NonNull
  public static NavDirections actionFragmentTodasMarcacoesToModalidadesFragment() {
    return new ActionOnlyNavDirections(R.id.action_fragmentTodasMarcacoes_to_modalidadesFragment);
  }
}
