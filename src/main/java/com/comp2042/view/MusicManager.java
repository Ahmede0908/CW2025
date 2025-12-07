package com.comp2042.view;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

/**
 * Manages background music playback for the Tetris game.
 * <p>
 * Handles loading and playing MP3 music files with support for looping,
 * volume control, and pause/resume functionality.
 * </p>
 *
 * @author TetrisJFX Team
 * @version 1.0
 */
public class MusicManager {
    
    private static MusicManager instance;
    private MediaPlayer mediaPlayer;
    private double volume = 0.7; // Default volume (0.0 to 1.0) - increased for better audibility
    private boolean isPlaying = false;
    
    private MusicManager() {
        // Private constructor for singleton pattern
    }
    
    /**
     * Gets the singleton instance of MusicManager.
     *
     * @return the MusicManager instance
     */
    public static MusicManager getInstance() {
        if (instance == null) {
            instance = new MusicManager();
        }
        return instance;
    }
    
    /**
     * Loads and plays background music from a resource file.
     * <p>
     * The music file should be placed in src/main/resources/ directory.
     * For example: src/main/resources/music/game_music.mp3
     * </p>
     *
     * @param musicFileName the name of the music file (e.g., "music/game_music.mp3")
     * @param loop whether to loop the music continuously
     */
    public void playMusic(String musicFileName, boolean loop) {
        // Stop any currently playing music
        stopMusic();
        
        try {
            // Load the music file from resources
            URL resource = getClass().getClassLoader().getResource(musicFileName);
            if (resource == null) {
                System.err.println("Music file not found: " + musicFileName);
                System.err.println("Make sure the file is in src/main/resources/ directory");
                System.err.println("Game will continue without music.");
                return;
            }
            
            String musicPath = resource.toExternalForm();
            System.out.println("Loading music from: " + musicPath);
            
            Media media = new Media(musicPath);
            mediaPlayer = new MediaPlayer(media);
            
            // Set volume
            mediaPlayer.setVolume(volume);
            System.out.println("Music volume set to: " + volume);
            
            // Loop if requested
            if (loop) {
                mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            } else {
                mediaPlayer.setCycleCount(1);
            }
            
            // Handle errors
            mediaPlayer.setOnError(() -> {
                System.err.println("Error playing music: " + 
                    (mediaPlayer.getError() != null ? mediaPlayer.getError().getMessage() : "Unknown error"));
                System.err.println("Game will continue without music.");
                isPlaying = false;
            });
            
            // Wait for media to be ready before playing
            mediaPlayer.setOnReady(() -> {
                System.out.println("Music is ready to play");
                if (!isPlaying) {
                    mediaPlayer.play();
                    isPlaying = true;
                    System.out.println("Music playback started");
                }
            });
            
            // Also handle when media starts playing
            mediaPlayer.setOnPlaying(() -> {
                System.out.println("Music is now playing - Status: " + mediaPlayer.getStatus());
                System.out.println("Current volume: " + mediaPlayer.getVolume());
                System.out.println("Media duration: " + mediaPlayer.getTotalDuration());
            });
            
            // Handle when media stops
            mediaPlayer.setOnStopped(() -> {
                System.out.println("Music stopped");
                isPlaying = false;
            });
            
            // Handle when media ends (for non-looping)
            mediaPlayer.setOnEndOfMedia(() -> {
                System.out.println("Music reached end");
                if (!loop) {
                    isPlaying = false;
                }
            });
            
            // Check if there's an error with the media itself
            if (media.getError() != null) {
                System.err.println("Media error: " + media.getError().getMessage());
                return;
            }
            
            // Start playing - OnReady will handle it if not ready yet
            System.out.println("Attempting to play music...");
            mediaPlayer.play();
            
        } catch (IllegalAccessError | Exception e) {
            // Handle module access errors gracefully
            System.err.println("Failed to load music (JavaFX media module issue): " + e.getMessage());
            System.err.println("Game will continue without music.");
            System.err.println("To enable music, add ALL these VM arguments to your run configuration:");
            System.err.println("  --add-opens javafx.media/javafx.scene.media=ALL-UNNAMED");
            System.err.println("  --add-exports javafx.media/com.sun.media.jfxmedia=ALL-UNNAMED");
            System.err.println("  --add-opens javafx.base/com.sun.javafx=ALL-UNNAMED");
            isPlaying = false;
            // Don't rethrow - allow game to continue
        }
    }
    
    /**
     * Stops the currently playing music.
     */
    public void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
            isPlaying = false;
        }
    }
    
    /**
     * Pauses the currently playing music.
     */
    public void pauseMusic() {
        if (mediaPlayer != null && isPlaying) {
            mediaPlayer.pause();
            isPlaying = false;
        }
    }
    
    /**
     * Resumes the paused music.
     */
    public void resumeMusic() {
        if (mediaPlayer != null && !isPlaying) {
            mediaPlayer.play();
            isPlaying = true;
        }
    }
    
    /**
     * Sets the volume for the music.
     *
     * @param volume the volume level (0.0 to 1.0, where 1.0 is maximum)
     */
    public void setVolume(double volume) {
        this.volume = Math.max(0.0, Math.min(1.0, volume)); // Clamp between 0.0 and 1.0
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(this.volume);
        }
    }
    
    /**
     * Gets the current volume level.
     *
     * @return the volume level (0.0 to 1.0)
     */
    public double getVolume() {
        return volume;
    }
    
    /**
     * Checks if music is currently playing.
     *
     * @return true if music is playing, false otherwise
     */
    public boolean isPlaying() {
        return isPlaying;
    }
}

