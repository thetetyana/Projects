import java.io.*;
import java.net.*;

public class Assign2 {

    public static String cyrillicConverter(char ch) {
        switch (ch) {
            case '\u0410': return ("A");
            case '\u0411': return ("B");
            case '\u0412': return ("V");
            case '\u0413': return ("G");
            case '\u0414': return ("D");
            case '\u0415': return ("Ye");
            case '\u0416': return ("Zh");
            case '\u0417': return ("Z");
            case '\u0418': return ("I");
            case '\u0419': return ("J");
            case '\u041A': return ("K");
            case '\u041B': return ("L");
            case '\u041C': return ("M");
            case '\u041D': return ("N");
            case '\u041E': return ("O");
            case '\u041F': return ("P");
            case '\u0420': return ("R");
            case '\u0421': return ("S");
            case '\u0422': return ("T");
            case '\u0423': return ("U");
            case '\u0424': return ("F");
            case '\u0425': return ("H");
            case '\u0426': return ("Ts");
            case '\u0427': return ("Ch");
            case '\u0428': return ("Sh");
            case '\u0429': return ("Shch");
            case '\u042A': return ("");
            case '\u042B': return ("Y");
            case '\u042C': return ("");
            case '\u042D': return ("Ye");
            case '\u042E': return ("Yu");
            case '\u042F': return ("Ya");

            case '\u0430': return ("a");
            case '\u0431': return ("b");
            case '\u0432': return ("v");
            case '\u0433': return ("g");
            case '\u0434': return ("d");
            case '\u0435': return ("ye");
            case '\u0436': return ("zh");
            case '\u0437': return ("z");
            case '\u0438': return ("i");
            case '\u0439': return ("j");
            case '\u043A': return ("k");
            case '\u043B': return ("l");
            case '\u043C': return ("m");
            case '\u043D': return ("n");
            case '\u043E': return ("o");
            case '\u043F': return ("p");
            case '\u0440': return ("r");
            case '\u0441': return ("s");
            case '\u0442': return ("t");
            case '\u0443': return ("u");
            case '\u0444': return ("f");
            case '\u0445': return ("h");
            case '\u0446': return ("ts");
            case '\u0447': return ("ch");
            case '\u0448': return ("sh");
            case '\u0449': return ("shch");
            case '\u044A': return ("");
            case '\u044B': return ("y");
            case '\u044C': return ("");
            case '\u044D': return ("ye");
            case '\u044E': return ("yu");
            case '\u044F': return ("ya");
        }
        return String.valueOf(ch);
    }

 public static void main(String[] args) {
        if (args.length > 0) {
            InputStream in = null;
            try {
                // Open the URL
                URL u = new URL(args[0]);

                System.out.println("Protocol:" + u.getProtocol());
                in = u.openStream();
                // buffer the input to increase performance
                in = new BufferedInputStream(in);

                // chain the InputStream to a Reader
                Reader r = new InputStreamReader(in, "UTF-8");

                Writer writer = new PrintWriter("out.html", "UTF-8");
                int c;
                
                // loop
                while ((c = r.read()) != -1) { 
                    writer.write(cyrillicConverter((char)c));
                    //writer.write((char)c);
                }
                writer.flush();
                writer.close();
            } catch (MalformedURLException ex) {
                System.err.println(args[0] + " is not a parseable URL");
            } catch (IOException ex) {
                System.err.println(ex);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        // ignore
                    }
                }
            }
        }
    }
}