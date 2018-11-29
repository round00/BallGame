package com.round0.ballgame;


import android.graphics.Color;
import android.widget.TextView;
import java.util.Random;

public class GameManager {

    //最大难度
    private final int MAX_LEVEL = 10;
    //随机数种子，用于产生随机坐标等
    Random random = new Random(System.currentTimeMillis());
    //控制更新障碍物时障碍物到球的位置
    private final int BALL_SECURITY_DIS = 100;
    //控制更新障碍物时障碍物到食物的位置
    private final int FOOD_SECURITY_DIS = 50;
    //一些颜色，用于随机食物的颜色
    private int[] colors = {
            Color.BLACK,
            Color.DKGRAY,
            Color.GRAY,
            Color.LTGRAY,
            Color.RED,
            Color.GREEN,
            Color.BLUE,
            Color.YELLOW,
            Color.CYAN,
            Color.MAGENTA
    };
    //屏幕尺寸
    private int screenHeight;
    private int screenWidth;
    //食物位置
    private int foodPosX;
    private int foodPosY;

    //球、食物、障碍物、分数显示板 view
    BallView ballView;
    FoodView foodView;
    BarrierView barrierView;
    TextView scoreView;

    //当前分数
    private int score;
    //当前障碍物的数量,会随难度增大而变多
    private int barriers_num;
    //障碍物的坐标
    private Position[] positions;
    //障碍物的半径大小随机的上下界
    private int barrierLowBound;
    private int barrierUpBound;

    //球最后更新时间戳，用于控制球的位移
    private long lastTimeStamp = 0;
    //球的当前x,y方向的速度
    private float speedX = 0;
    private float speedY = 0;
    //球当前的位置
    private int ballPosX = 0;
    private int ballPosY = 0;
    //由于速度的单位是m/s,而位移的单位是类似像素这种很小的单位，所以把速度在数值上放大一些
    private float ENLARGE = 100;

    public GameManager(BallView ballView, FoodView foodView,
                       BarrierView barrierView,TextView textView,
                       int screenHeight, int screenWidth) {
        this.ballView = ballView;
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
        this.foodView = foodView;
        this.barrierView = barrierView;
        this.scoreView = textView;
    }

    public void init(){
        score = 0;
        barriers_num = 2;
        barrierLowBound = 19;
        barrierUpBound = 20;
        initBallPos();
        updateFood();
        updateBarrires();
        showScore();
    }

    private void initBallPos(){
        speedX = speedY = 0;
        ballPosX = screenWidth/2;
        ballPosY = screenHeight/2;
        lastTimeStamp = 0;
        ballView.setPosition(ballPosX, ballPosY);
    }

    //更新球的位置，输入参数：x,y方向的加速度，直接把加速度当做球的速度
    public void updateBall(float accSpeedX, float accSpeedY){
        //获取当前时间戳
        long timeStamp = System.currentTimeMillis();

        if(lastTimeStamp == 0){
            lastTimeStamp = timeStamp;
            return;
        }
        //计算时间差
        float deltTime = (timeStamp-lastTimeStamp)*0.001f;
        lastTimeStamp = timeStamp;
        //计算当前速度
        speedX = accSpeedX;
        speedY = accSpeedY;
        //计算当前位置
        ballPosX = ballPosX + (int)(speedX*deltTime*ENLARGE);
        ballPosY = ballPosY + (int)(speedY*deltTime*ENLARGE);

        //控制球不会跑出屏幕
        if(ballPosX < 0){ballPosX = 0;speedX = 0;}
        if(ballPosX > screenWidth){ballPosX = screenWidth;speedX = 0;}
        if(ballPosY < 0){ballPosY = 0;speedY = 0;}
        if(ballPosY > screenHeight){ballPosY = screenHeight;speedY = 0;}

        ballView.setPosition(ballPosX, ballPosY);
        return;
    }

    //更新食物点
    private void updateFood(){
        foodPosX = random.nextInt(screenWidth-100)+50;
        foodPosY = random.nextInt(screenHeight-100)+50;
        int randomColor = colors[random.nextInt(colors.length)];
        foodView.setSizeColor(foodPosX, foodPosY, randomColor);
    }

    //更新障碍物
    private void updateBarrires(){
        int level = score/10 + 2;
        if(level > MAX_LEVEL)level = MAX_LEVEL;
        barriers_num = level;
        barrierUpBound = level*10;

        Position[] poss = new Position[barriers_num];
        for(int i = 0; i<barriers_num; ++i){
            Position pos = new Position(
                    getRandomInt(50,screenWidth-50, ballPosX, foodPosX),
                    getRandomInt(50,screenHeight-50, ballPosY, foodPosY),
                    random.nextInt(barrierUpBound-barrierLowBound)+barrierLowBound);
            poss[i] = pos;

        }
        positions = poss;
        barrierView.setPositions(positions);
    }

    //获取障碍物随机位置，同时控制障碍物的位置不会离球和食物太近
    private int getRandomInt(int lowBound, int upBound, int ballPos, int foodPos){
        int randomInt = random.nextInt(upBound-lowBound)+lowBound;
        while(Math.abs(ballPos - randomInt) < BALL_SECURITY_DIS||
                Math.abs(foodPos - randomInt) < FOOD_SECURITY_DIS ){
            randomInt = random.nextInt(upBound-lowBound)+lowBound;
        }
        return randomInt;
    }

    //0:nothing 1:eat a food 2:dead
    public int check(){
        //判断是否吃到食物
        if(isCover()){
            score++;
            updateFood();
            updateBarrires();
            showScore();
            return 1;
        }
        //判断是否碰到障碍物
        boolean bCoverBarrier = false;
        for(int i = 0; i<barriers_num; ++i){
            if(isTouch(positions[i].posX, positions[i].posY, positions[i].radius)){
                bCoverBarrier = true;
                break;
            }
        }
        if(bCoverBarrier){
            return 2;
        }

        return 0;
    }

    //判断是否吃了食物
    private boolean isCover(){
        int tmp = (ballPosX-foodPosX)*(ballPosX-foodPosX) +(ballPosY-foodPosY)*(ballPosY-foodPosY);
        float dis = (float) Math.sqrt((double) tmp);//两圆心距离
        if(dis+FoodView.RADIUS < BallView.RADIUS)return true;
        return false;
    }

    //判断是否接触到了障碍物
    private boolean isTouch(int barrierPosX, int barrierPosY, float barrierR){
        int tmp = (ballPosX-barrierPosX)*(ballPosX-barrierPosX) +(ballPosY-barrierPosY)*(ballPosY-barrierPosY);
        float dis = (float) Math.sqrt((double) tmp);//两圆心距离
        if(dis<BallView.RADIUS+barrierR)return true;
        return false;
    }

    //更新显示分数
    private void showScore(){
        StringBuilder sb = new StringBuilder();
        sb.append("score:" +score);
        scoreView.setText(sb.toString());
    }
}
