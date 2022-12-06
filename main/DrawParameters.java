package main;



import java.awt.*;

public class DrawParameters {
    
    public static Cursor DEFAULT_CURSOR=new Cursor(Cursor.DEFAULT_CURSOR);
    public static Cursor MOVE_CURSOR=new Cursor(Cursor.MOVE_CURSOR);
    public static Cursor NW_RESIZE_CURSOR=new Cursor(Cursor.NW_RESIZE_CURSOR);
    public static Cursor NE_RESIZE_CURSOR=new Cursor(Cursor.NE_RESIZE_CURSOR);
    public static Cursor SE_RESIZE_CURSOR=new Cursor(Cursor.SE_RESIZE_CURSOR );
    public static Cursor SW_RESIZE_CURSOR=new Cursor(Cursor.SW_RESIZE_CURSOR );
    public static Cursor N_RESIZE_CURSOR=new Cursor(Cursor.N_RESIZE_CURSOR );
    public static Cursor E_RESIZE_CURSOR=new Cursor(Cursor.E_RESIZE_CURSOR);
    public static Cursor S_RESIZE_CURSOR=new Cursor(Cursor.S_RESIZE_CURSOR);
    public static Cursor W_RESIZE_CURSOR=new Cursor(Cursor.W_RESIZE_CURSOR);
    public static Cursor CROSSHAIR_CURSOR=new Cursor(Cursor.CROSSHAIR_CURSOR);
    public static Cursor TEXT_CURSOR=new Cursor(Cursor.TEXT_CURSOR);
    public static Cursor LEFTRIGHT_CURSOR=null;
    public static Cursor UPDOWN_CURSOR=null;
    public static Cursor CURRENT_CURSOR=DEFAULT_CURSOR;
    
    public final static int A3=0;
    public final static int A4=1;
    public final static int A5=2;
    public final static int B4=3;
    public final static int B5=4;
    public final static int PostCardJP=5;
    public final static int Letter=6;
    public final static int Legal=7;
    public final static int Tabloid=8;

    public final static Dimension A3_Size=new Dimension(297, 420);
    public final static Dimension A4_Size=new Dimension(210, 297);
    public final static Dimension A5_Size=new Dimension(148, 210);
    public final static Dimension B4_Size=new Dimension(257, 364);
    public final static Dimension B5_Size=new Dimension(182, 257);
    public final static Dimension PostCardJP_Size=new Dimension(100, 148);
    public final static Dimension Letter_Size=new Dimension(216, 279);
    public final static Dimension Legal_Size=new Dimension(216, 355);
    public final static Dimension Tabloid_Size=new Dimension(279, 432);
    
    public final static Dimension[] SheetSizesMM={A3_Size, A4_Size, A5_Size, B4_Size, B5_Size, 
                         PostCardJP_Size, Letter_Size, Legal_Size, Tabloid_Size};
    public final static String[] SheetSizeString={"A3","A4","A5","B4","B5","PostCardJP",
                        "Letter","Legal","Tabloid",};
    
    public final static int LandScape=0;
    public final static int Portrait=1;
    public final static String[] SheetOrientationString={"LandScape", "Portrait"};

    public final static double InchToMM=25.4; //(mm)
    public final static int InchToPixels=96;
    
    public final static String[] LF={"\n", "↲", " "};
            
    public final static Font DefaultFont=new Font(Font.DIALOG, Font.PLAIN, 12);
    public final static Font smallFont=new Font(Font.DIALOG, Font.PLAIN, 11);
    public final static Font Font12Bold=new Font(Font.DIALOG, Font.BOLD, 12);
    public final static Font Font13Plain=new Font(Font.DIALOG, Font.PLAIN, 13);
    public final static Font Font13Bold=new Font(Font.DIALOG, Font.BOLD, 13);
    public final static Font Font14Plain=new Font(Font.DIALOG, Font.PLAIN, 14);
    public final static Font Font14Bold=new Font(Font.DIALOG, Font.BOLD, 14);
    
    public final static int SelectionBoxOffset=6;
    public final static int DrawAreaOffset=40;
    public final static int TempLineLength=30;
    public final static int Mark_NormalSize=6;
    public final static int Mark_SmallSize=4;
    public final static double ConnectionTolerance=4d;
    public final static double ConnectionSmallTolerance=2d;
    public final static double ClosedTolerance=3d;
    public final static double Sampling_Pitch=5d;
    public final static Dimension MinTextArea=new Dimension(10, 10);
    public final static float DefaultAlpha=0.95f;
    public final static float MoveAlpha=0.6f;

 //View Parameters
    public final static int DrawOnScreen=0;
    public final static int DrawOnPrinter=1;
    public final static int DrawOnImage=2;

    public static int DrawMode=DrawOnScreen;
    public static double Scale=1.0d;
    public static int SheetSize=DrawParameters.B5;
    public static int SheetOrientation=LandScape;
    
 // Default settings
    public static boolean AUTO_ALIGN=true;
    public static boolean ENABLE_CONNECTOR=true;
    public static boolean DRAW_NODE_POINTS=false;
    public static boolean DRAW_CHARACTERISTIC_POINTS=false;
    public static boolean DRAW_TEXTLAYOUT=false;
    public static boolean DRAW_BOUNDINGBOX=false;
    public static boolean DRAW_UNSELECTABLEAREA=false;
    
    
    public DrawParameters(){
        this.createCustomCorsor();
    }
    
    public static double getScale(){
        return Scale;
    }
    
    public static Dimension getSheetSizeByPixel(){
        int sheetType=DrawParameters.SheetSize;
        Dimension sheetSize=DrawParameters.SheetSizesMM[sheetType];
        sheetSize=new Dimension((int)sheetSize.getHeight(), (int)sheetSize.getWidth());
        int sheetOrientation=DrawParameters.SheetOrientation;
        double inch=DrawParameters.InchToMM;
        int pixelsPerInch=DrawParameters.InchToPixels;
        int drawPanelW=(int)(sheetSize.getWidth()/inch*pixelsPerInch);
        int drawPanelH=(int)(sheetSize.getHeight()/inch*pixelsPerInch);
        if(sheetOrientation==DrawParameters.Portrait){
            int temp=drawPanelW;
            drawPanelW=drawPanelH;
            drawPanelH=temp;
        }
        return (new Dimension(drawPanelW, drawPanelH));
    }

    private void createCustomCorsor(){
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image image0 = toolkit.getImage("images/cursor_leftright.gif");
        LEFTRIGHT_CURSOR = toolkit.createCustomCursor(image0, new Point(0,0), "LEFTRIGHT_CURSOR");
        Image image1 = toolkit.getImage("images/connectableShapeMark16T.png");
        UPDOWN_CURSOR = toolkit.createCustomCursor(image1, new Point(0,0), "UPDOWN_CURSOR");
        //System.out.println("image0="+image0+", image1="+image1);
    }
}
