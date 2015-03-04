public class GamePanel{
    

    Matter[][] matter;
    
    //This could be included in the above array as an alternative
    Player player;

    public GamePanel(int level){
        
    }

    public void paint(Graphics g){
        //Add collision detection, camera motion, and other movement calls here.
        //Or they can be put in another method that will separately update the figures, in another thread,
        //while this method is repeatedly called by itself with or without the changes from the other update method
        
        //also this method/class will have to interact with the camera so that it knows what part of the map to paint
        //this panel should probably use its own camera so there should be a global Camera c = new Camera() or something
        
        //draws everything onto the screen
        for(int i = 0; i < matter.length; i++){
            for(int j = 0; j < matter[0].length; j++){
               //we're gonna have to paint the square icon wherever and get its location,
               //which should all be in the class so I think it'd be something like:
               g.drawImage(matter[i][j].getImage(), matter[i][j].getX(), matter[i][j].getY(), null);
            }
        }
    }
}
