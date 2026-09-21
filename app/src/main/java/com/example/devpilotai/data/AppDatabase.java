package com.example.devpilotai.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.devpilotai.data.model.Challenge;
import com.example.devpilotai.data.model.ChallengeDao;
import com.example.devpilotai.data.model.ChallengeResult;
import com.example.devpilotai.data.model.ChallengeResultDao;
import com.example.devpilotai.data.model.ChatMessage;
import com.example.devpilotai.data.model.ChatMessageDao;
import com.example.devpilotai.data.model.Folder;
import com.example.devpilotai.data.model.FolderDao;
import com.example.devpilotai.data.model.Note;
import com.example.devpilotai.data.model.NoteDao;
import com.example.devpilotai.data.model.Snippet;
import com.example.devpilotai.data.model.SnippetDao;
import com.example.devpilotai.data.model.UserStats;
import com.example.devpilotai.data.model.UserStatsDao;

@Database(entities = {Snippet.class, Note.class, Folder.class, Challenge.class, ChallengeResult.class, ChatMessage.class, UserStats.class}, version = 5, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract SnippetDao snippetDao();
    public abstract NoteDao noteDao();
    public abstract FolderDao folderDao();
    public abstract ChallengeDao challengeDao();
    public abstract ChallengeResultDao challengeResultDao();
    public abstract ChatMessageDao chatMessageDao();
    public abstract UserStatsDao userStatsDao();

    public static AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "devpilot_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
