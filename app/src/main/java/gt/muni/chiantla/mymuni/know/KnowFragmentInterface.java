package gt.muni.chiantla.mymuni.know;

/**
 * La interface que modela un Fragmento de la seccion conoce
 */
public interface KnowFragmentInterface {
    /**
     * Obtiene el titulo de la seccion actual
     *
     * @return el titulo
     */
    String getTitle();

    /**
     * Obtiene el layout de la seccion actual
     *
     * @return el id del layout
     */
    int getLayout();
}
