import java.io.*;
import sun.audio.*;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws java.lang.Exception {
        int currentsong = 0;
        String ph;
        boolean running = true;
        Scanner input = new Scanner(System.in);
        ArrayList<String> soundfiles = new ArrayList<>();
        ArrayList<String> songnames = new ArrayList<>();
        ArrayList<Integer> randomlyused = new ArrayList<>();

        File curdir = new File("src/com/rohan/songs"); // FIX
        File[] files = curdir.listFiles();
        for(File f : files) {
            if(f.isFile()) {
                songnames.add(f.getName());
                soundfiles.add("src/com/rohan/songs/" + f.getName()); //FIX
            }
        }


        System.out.println("~~~~~~~~~~MENU~~~~~~~~~~\n\t1. Play All Songs\n\t2. Shuffle Songs/Play\n\t3. Play One Song\n\t4. Create Song Queue\n\t5. View Songs\n");

        while(running) {
            System.out.println("\nType a Number: ");
            ph = input.nextLine();
            switch(ph) {
                case "1":
                    MainLoop(soundfiles, randomlyused, songnames, currentsong, 0);
                    break;
                case "2":
                    MainLoop(soundfiles, randomlyused, songnames, currentsong, 1);
                    break;
                case "3":
                    MainLoop(soundfiles, randomlyused, songnames, currentsong, 0, "OneSong");
                    break;
                case "4":
                    MainLoop(soundfiles, randomlyused, songnames, currentsong, 0, "Queue");
                    break;
                case "5":
                    MainLoop(soundfiles, randomlyused, songnames, currentsong, 0, "ViewSongs");
                    break;
            }
        }
    }

    private static void MainLoop(ArrayList<String> soundfiles, ArrayList<Integer> randomlyused, ArrayList<String> songnames, int currentsong, int shuffle, String... extras) throws java.lang.Exception {

        if(extras.length > 0) {
            String ph;
            Scanner input_ = new Scanner(System.in);
            switch(extras[0]) {
                case "OneSong":
                    if(extras.length > 1) {
                        ph = extras[1];
                        ph = ph.replace(".wav", "");

                    } else {
                        System.out.println("Song Name? : ");
                        ph = input_.nextLine();
                    }
                    if(songnames.contains(ph + ".wav")) {
                        System.out.print("\r==========Song #" + (songnames.indexOf(ph + ".wav") + 1) + " - " + (ph + ".wav") + " =========\n\n");
                        playSong(soundfiles, soundfiles.get(songnames.indexOf(ph + ".wav")));
                        TimeUnit.SECONDS.sleep(calculateDuration(soundfiles, songnames.indexOf(ph + ".wav") + 1));
                    }
                    return;
                case "Queue":
                    LinkedList<String> queuedsongs = new LinkedList<>();
                    System.out.println("Type each song and press 'ENTER' When done, type 'DONE'. \n[To view the list, type viewlist]\nTo quit, type 'q'\n");
                    while(true) {
                        System.out.println(">> ");
                        ph = input_.nextLine();
                        if(songnames.contains(ph + ".wav")) {
                            queuedsongs.add(ph + ".wav");
                        } else if(ph.equals("DONE") || ph.equals("viewlist") || ph.equals("q")) {
                            if(ph.equals("DONE")) {
                                Iterator<String> i = queuedsongs.iterator();
                                while(i.hasNext()) {
                                    MainLoop(soundfiles, randomlyused, songnames, currentsong, 0, "OneSong", i.next());
                                }
                            } else if (ph.equals("viewlist")) {
                                System.out.print("\n");
                                MainLoop(soundfiles, randomlyused, songnames, currentsong, 0, "ViewSongs");
                                System.out.print("\n");
                            } else if (ph.equals("q")) {
                                break;
                            }
                        } else {
                            System.out.println("Song or command not recognized.");
                        }
                    }
                    break;

                case "ViewSongs":
                    for(String element : songnames) {
                        System.out.println("\t" + element);
                    }
                    return;
            }

        } else {
            int random = new Random().nextInt(soundfiles.size());
            System.out.print("==========Song List is Being Played=========\n\n");

            for(int item = 0; item < soundfiles.size(); item++) {
                currentsong++;
                if(shuffle == 0) {
                    playSong(soundfiles, soundfiles.get(item));
                    System.out.print("\r\tSong #" + currentsong + " || Name: " + songnames.get(currentsong-1));
                    TimeUnit.SECONDS.sleep(calculateDuration(soundfiles, currentsong));
                } else if(shuffle == 1) {
                    currentsong = random+1;
                    playSong(soundfiles, soundfiles.get(random));
                    System.out.print("\r\tSong #" + currentsong + " || Name: " + songnames.get(random));
                    TimeUnit.SECONDS.sleep(calculateDuration(soundfiles, currentsong));
                    while(true) {
                        random = new Random().nextInt(soundfiles.size());
                        if(randomlyused.contains(random)) {

                        } else {
                            break;
                        }
                    }
                }

            }
        }//end
    }

    private static void playSong(ArrayList<String> music, String SongNo) throws Exception {
        InputStream in = new FileInputStream(SongNo);
        AudioStream audio = new AudioStream(in);
        AudioPlayer.player.start(audio);

    }

    private static int calculateDuration(ArrayList<String> soundfiles, int currentsong) throws java.lang.Exception {
        File temp = new File(soundfiles.get(currentsong-1));
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(temp);
        AudioFormat format = audioInputStream.getFormat();
        long frames = audioInputStream.getFrameLength();
        double durationInSeconds = (frames + 0.0) / format.getFrameRate();
        return (int) durationInSeconds;
    }

}
