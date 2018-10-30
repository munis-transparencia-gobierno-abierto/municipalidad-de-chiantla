package gt.muni.chiantla.content.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import gt.muni.chiantla.content.PageItem;

@Dao
public interface PageItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertItems(PageItem... items);

    @Query("SELECT * FROM pageitem WHERE id = :id LIMIT 1")
    PageItem findById(int id);

    @Query("SELECT * FROM pageitem WHERE pageId = :id ORDER BY orderNo")
    List<PageItem> findByPageId(int id);
}
