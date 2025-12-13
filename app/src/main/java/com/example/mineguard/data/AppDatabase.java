package com.example.mineguard.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.mineguard.alarm.model.AlarmItem;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// 如果以后有其他表，加到 entities = {AlarmItem.class, Other.class}
@Database(entities = {AlarmItem.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract AlarmDao alarmDao();

    private static volatile AppDatabase INSTANCE;

    // 创建一个包含4个线程的线程池，用于后台写数据库
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "mineguard_database")
                            // .fallbackToDestructiveMigration() // 如果数据库结构变了，清空重来 (开发期可以用)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}