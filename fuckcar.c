#include <reg52.h>
#define LOOP_0(f) f##(0)
#define LOOP_1(f) LOOP_0(f) f##(1)
#define LOOP_2(f) LOOP_1(f) f##(2)
#define LOOP_3(f) LOOP_2(f) f##(3)
#define LOOP_4(f) LOOP_3(f) f##(4)
#define LOOP_HELPER(n, f) LOOP_##n(f)
#define LOOP(n, f) LOOP_HELPER(n, f)
#define CMD(n, f) f##(n)

#define MARCO_RG_F(n) if (e==n) return R##n;
#define MARCO_LED_DEFINE(n) sbit LED##n = P0^##n;
#define MARCO_LED_F(n) if (e==n) return LED##n = s;
// #define MARCO_RL 
LOOP(4, MARCO_LED_DEFINE)

typedef bit bool;
typedef unsigned char uint8;
typedef signed char int8;
typedef unsigned int uint16;
typedef signed int int16;

sfr T2MOD=0xc9;
//红外地址接口定义
sbit A0 = P1^2;
sbit A1 = P1^3;
sbit A2 = P1^4;

//红外传感器接收信号口定义（接收--0 未接收--1）
sbit R0 = P3^6;	//左
sbit R1 = P2^5;	//左前
sbit R2 = P2^6;	//中
sbit R3 = P2^7;	//右前
sbit R4 = P3^7;	//右

//定义红外传感器检测状态全局位变量 （L--Left R--Right C--Center LU--左前方 RU--右前方）
bit irL = 0,irR = 0,irC = 0,irLU = 0,irRU = 0;

//定义电机引脚控制变量 	ML左电机  MR右电机
sbit ML1=P2^0;
sbit ML2=P2^1;
sbit MR1=P2^2;
sbit MR2=P2^3;

//设置变量记录左右电机产生的脉冲数
unsigned char intCountL=0,intCountR=0;
unsigned int i, j, k;	  

//红外发射控制宏定义(传入传感器组号)
void IR_ON(uint8 n) { 
    A0 = (n)&0x01, A1 = (n)&0x02, A2 = (n)&0x04;
    if ((n)==5) A0=A1=A2=1; 
}

uint8 RG(uint8 e) { LOOP(4, MARCO_RG_F) }
uint8 LED(uint8 e, uint8 s) { LOOP(4, MARCO_LED_F) return -1;}
//初始化定时器T2
void initT2(uint16 us)
{
	EA=1;
	T2MOD=0;
	TCON=0;
	TL2=RCAP2L=(65536-us)%256; //500ms
	TH2=RCAP2H=(65536-us)/256;
	TR2=ET2=1;
}
void runML(bit f) {
    if (f) {
        ML1 = 1; ML2 = 0;
    } else {
        ML1 = 0; ML2 = 1;
    }
}
void runMR(bit f) {
    if (f) {
        MR1 = 1; MR2 = 0;
    } else {
        MR1 = 0; MR2 = 1;
    }
}
void stopMR() { MR1 = MR2 = 1; }
void stopML() { ML1 = ML2 = 1; }
void go() {
    unsigned int l = 50;
    for (i = 0, j = 0; i < l; i++, j++) {
        if (i < 50) 
        runMR(1);
        else stopMR();
        if (j < 50) {
            runML(0);
        } else stopML();
    }
}
void turn() {
    stopMR();
    for (i = 0; i < 50; i++) runML(0);
}
void main()
{
	initT2(8000);
	while(1){
        for (k = 0; k < 1000; k++) go();
        for (k = 0; k < 300; k++) turn();
    };
}
void scan() interrupt 5 {
    static uint8 n = 0;
    TF2 = 0;
    (RG(n-1))?LED(n-1, 1):LED(n-1, 0);
    IR_ON(n);
    n = (n+1)%6;
}