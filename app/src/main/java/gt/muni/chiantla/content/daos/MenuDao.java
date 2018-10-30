package gt.muni.chiantla.content.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import gt.muni.chiantla.content.Menu;

@Dao
public interface MenuDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMenus(Menu... menus);

    @Query("SELECT * FROM menu WHERE id = :id LIMIT 1")
    Menu findById(int id);

    @Query("SELECT * FROM menu WHERE menu = :id")
    List<Menu> findByMenuId(int id);
}
