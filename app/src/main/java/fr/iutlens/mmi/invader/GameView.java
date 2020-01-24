package fr.iutlens.mmi.invader;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import fr.iutlens.mmi.invader.utils.RefreshHandler;
import fr.iutlens.mmi.invader.utils.SpriteSheet;
import fr.iutlens.mmi.invader.utils.TimerAction;


public class GameView extends View implements TimerAction, View.OnTouchListener {
    private RefreshHandler timer;

    // taille de l'écran virtuel
    public final static int SIZE_X = 2000;
    public final static int SIZE_Y = 2400;

    // transformation (et son inverse)
    private Matrix transform;
    private Matrix reverse;

    //liste des sprites à afficher


    private Armada armada;
    private Canon canon;
    private List<Projectile> missile;
    private List<Projectile> laser;
    private float oldX= -1000;


    public GameView(Context context) {
        super(context);
        init(null, 0);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    /**
     * Initialisation de la vue
     *
     * Tous les constructeurs (au-dessus) renvoient ici.
     *
     * @param attrs
     * @param defStyle
     */
    private void init(AttributeSet attrs, int defStyle) {

        // Chargement des feuilles de sprites
        SpriteSheet.register(R.mipmap.alien,2,1,this.getContext());
        SpriteSheet.register(R.mipmap.missile,1,1,this.getContext());
        SpriteSheet.register(R.mipmap.laser,1,1,this.getContext());
        SpriteSheet.register(R.mipmap.rocket,1,1,this.getContext());
        SpriteSheet.register(R.mipmap.background2,1,1,this.getContext());

        transform = new Matrix();
        reverse = new Matrix();

        missile = new ArrayList<>();
        laser = new ArrayList<>();

        armada = new Armada(R.mipmap.alien,missile);
        canon = new Canon(R.mipmap.rocket,800, 2200,laser);


//        hero = new Hero(R.drawable.running_rabbit,SPEED);



        // Gestion du rafraichissement de la vue. La méthode update (juste en dessous)
        // sera appelée toutes les 30 ms
        timer = new RefreshHandler(this);
        timer.scheduleRefresh(30);

        this.setOnTouchListener(this);
    }


    public static void act(List list){
        Iterator it = list.iterator();
        while (it.hasNext()) if (((Sprite) it.next()).act()) it.remove();
    }
    /**
     * Mise à jour (faite toutes les 30 ms)
     */
    @Override
    public void update() {
        if (this.isShown()) { // Si la vue est visible
            timer.scheduleRefresh(15); // programme le prochain rafraichissement

            armada.testIntersection(laser);
            armada.act();
            canon.act();

            act(missile);
            act(laser);

            invalidate(); // demande à rafraichir la vue
        }
    }

    /**
     * Méthode appelée (automatiquement) pour afficher la vue
     * C'est là que l'on dessine le décor et les sprites
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // On met une couleur de fond
        canvas.drawColor(0xff000077);



        // On choisit la transformation à appliquer à la vue i.e. la position
        // de la "camera"

        canvas.concat(transform);


        canvas.save();
        canvas.scale(8,8);
        SpriteSheet.get(R.mipmap.background2).paint(canvas,0,0,0);

        canvas.restore();

        for(Sprite s : missile){
            s.paint(canvas);
        }
        for(Sprite s : laser){
            s.paint(canvas);
        }
        canon.paint(canvas);
        armada.paint(canvas);


        // Dessin des différents éléments
/*        level.paint(canvas,current_pos);

        float x = 1;
        float y = hero.getY();
        hero.paint(canvas,level.getX(x),level.getY(y));
*/
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        setZoom(w, h);
    }

    /***
     * Calcul du centrage du contenu de la vue
     * @param w
     * @param h
     */
    private void setZoom(int w, int h) {
        if (w<=0 ||h <=0) return;

        // Dimensions dans lesquelles ont souhaite dessiner
        RectF src = new RectF(0,0,SIZE_X,SIZE_Y);

        // Dimensions à notre disposition
        RectF dst = new RectF(0,0,w,h);

        // Calcul de la transformation désirée (et de son inverse)
        transform.setRectToRect(src,dst, Matrix.ScaleToFit.CENTER);
        transform.invert(reverse);
    }

    public void onLeft() {
        canon.setDirection(-1);
    }

    public void onRight(){
        canon.setDirection(+1);
    }

    public void onFire(){
        canon.fire();

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_MOVE ||
                motionEvent.getAction() == MotionEvent.ACTION_DOWN){
            float[] coord = {motionEvent.getX(),motionEvent.getY()};
            reverse.mapPoints(coord);



            if (oldX != -1000) canon.x += (coord[0]-oldX);
            oldX =  coord[0];
            return true;
        } else if (motionEvent.getAction()== MotionEvent.ACTION_UP){
            oldX = -1000;
        }

        return false;
    }
}
