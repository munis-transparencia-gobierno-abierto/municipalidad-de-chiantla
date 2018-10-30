package gt.muni.chiantla.content.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import gt.muni.chiantla.content.Page;

@Dao
public interface PageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPages(Page... pages);

    @Query("SELECT * FROM page WHERE id = :id LIMIT 1")
    Page findById(int id);

    @Query("SELECT * FROM page WHERE menu = :id")
    List<Page> findByMenuId(int id);
}
