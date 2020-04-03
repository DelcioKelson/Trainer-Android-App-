package pmd.di.ubi.pt.projectofinal;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;

public class FragmentMarcacoesDirections {
  private FragmentMarcacoesDirections() {
  }

  @NonNull
  public static NavDirections actionMarcacoesFragmentToDetalhesMarcacaoFragment() {
    return new ActionOnlyNavDirections(R.id.action_marcacoesFragment_to_detalhesMarcacaoFragment);
  }
}
