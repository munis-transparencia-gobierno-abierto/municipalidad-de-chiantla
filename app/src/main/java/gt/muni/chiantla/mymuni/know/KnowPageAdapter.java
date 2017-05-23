package gt.muni.chiantla.mymuni.know;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


/**
 * Adaptador para la sección de conoce.
 * @author Ludiverse
 * @author Innerlemonade
 */
public class KnowPageAdapter extends FragmentStatePagerAdapter {
    public KnowPageAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return DemographicsFragment.newInstance();
            case 1:
                return RegionFragment.newInstance();
            case 2:
                return IndicatorsFragment.newInstance();
            case 3:
                return LanguagesFragment.newInstance();
            case 4:
                return PopulationFragment.newInstance();
            case 5:
                return EconomyFragment.newInstance();
            case 6:
                return MissionFragment.newInstance();
            default:
                return DemographicsFragment.newInstance();
        }
    }

    @Override
    public int getCount() {
        return 7;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "OBJECT " + (position + 1);
    }
}
