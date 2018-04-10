#include "block.bbh"
#include "clock.bbh"


threaddef  #define NO_LIEN  99
threaddef #define UNKNOWN		255

threadvar byte position[2];
threadvar byte tab[2];

threadvar  uint8_t lien;
threadvar  uint8_t nbreWaitedAnswers;
threadvar byte xplusBorder;
threadvar byte yplusBorder;
threadvar byte fpos;
threadvar byte forme;
threadvar byte rota;
threadvar byte countspawn;
threadvar byte spawn;

threadvar int sample[5][4][9];


threaddef  #define MYCHUNKS 12
threadextern Chunk* thisChunk;
threadvar  Chunk myChunks[MYCHUNKS];

 /***************/
 /** functions **/
 byte goMessageHandler(void);
 byte sendBackChunk(PRef p);
 byte backMessageHandler(void);
 byte sendCoordChunk(PRef p);
 byte coordMessageHandler(void);
 byte sendExecOn(PRef p, byte px, byte py, byte donnee, byte fonc);
 byte execOn(void);
 byte Spawn(void);
 byte getSpawn(uint8_t donnee,uint8_t t);
 byte sendShape(PRef p);
 byte shapeMessageHandler(void);
 byte goingDown(void);


/******************************/
 void myMain(void) {

   delayMS(200);
   lien=NO_LIEN;
   nbreWaitedAnswers=0;
   position[0] = 0;
   position[1] = 0;
   xplusBorder = WEST;
   yplusBorder = UP;
   fpos = UNKNOWN;
 	 forme = UNKNOWN;
 	 rota = UNKNOWN;


  sample[0][0][0]=1; sample[0][0][1]=1; sample[0][0][2]=1; sample[0][0][3]=1; sample[0][0][4]=1; sample[0][0][5]=1; sample[0][0][6]=1; sample[0][0][7]=1; sample[0][0][8]=1;
 	sample[0][1][0]=1; sample[0][1][1]=1; sample[0][1][2]=1; sample[0][1][3]=1; sample[0][1][4]=1; sample[0][1][5]=1; sample[0][1][6]=1; sample[0][1][7]=1; sample[0][1][8]=1;
 	sample[0][2][0]=1; sample[0][2][1]=1; sample[0][2][2]=1; sample[0][2][3]=1; sample[0][2][4]=1; sample[0][2][5]=1; sample[0][2][6]=1; sample[0][2][7]=1; sample[0][2][8]=1;
 	sample[0][3][0]=1; sample[0][3][1]=1; sample[0][3][2]=1; sample[0][3][3]=1; sample[0][3][4]=1; sample[0][3][5]=1; sample[0][3][6]=1; sample[0][3][7]=1; sample[0][3][8]=1;

 	sample[1][0][0]=0; sample[1][0][1]=1; sample[1][0][2]=0; sample[1][0][3]=0; sample[1][0][4]=1; sample[1][0][5]=0; sample[1][0][6]=0; sample[1][0][7]=1; sample[1][0][8]=1;
 	sample[1][1][0]=0; sample[1][1][1]=0; sample[1][1][2]=0; sample[1][1][3]=1; sample[1][1][4]=1; sample[1][1][5]=1; sample[1][1][6]=1; sample[1][1][7]=0; sample[1][1][8]=0;
 	sample[1][2][0]=1; sample[1][2][1]=1; sample[1][2][2]=0; sample[1][2][3]=0; sample[1][2][4]=1; sample[1][2][5]=0; sample[1][2][6]=0; sample[1][2][7]=1; sample[1][2][8]=0;
 	sample[1][3][0]=0; sample[1][3][1]=0; sample[1][3][2]=1; sample[1][3][3]=1; sample[1][3][4]=1; sample[1][3][5]=1; sample[1][3][6]=0; sample[1][3][7]=0; sample[1][3][8]=0;

 	sample[2][0][0]=1; sample[2][0][1]=0; sample[2][0][2]=0; sample[2][0][3]=1; sample[2][0][4]=1; sample[2][0][5]=0; sample[2][0][6]=0; sample[2][0][7]=1; sample[2][0][8]=0;
 	sample[2][1][0]=0; sample[2][1][1]=1; sample[2][1][2]=1; sample[2][1][3]=1; sample[2][1][4]=1; sample[2][1][5]=0; sample[2][1][6]=0; sample[2][1][7]=0; sample[2][1][8]=0;
 	sample[2][2][0]=0; sample[2][2][1]=1; sample[2][2][2]=0; sample[2][2][3]=0; sample[2][2][4]=1; sample[2][2][5]=1; sample[2][2][6]=0; sample[2][2][7]=0; sample[2][2][8]=1;
 	sample[2][3][0]=0; sample[2][3][1]=0; sample[2][3][2]=0; sample[2][3][3]=0; sample[2][3][4]=1; sample[2][3][5]=1; sample[2][3][6]=1; sample[2][3][7]=1; sample[2][3][8]=0;

 	sample[3][0][0]=0; sample[3][0][1]=1; sample[3][0][2]=0; sample[3][0][3]=1; sample[3][0][4]=1; sample[3][0][5]=1; sample[3][0][6]=0; sample[3][0][7]=0; sample[3][0][8]=0;
 	sample[3][1][0]=0; sample[3][1][1]=1; sample[3][1][2]=0; sample[3][1][3]=0; sample[3][1][4]=1; sample[3][1][5]=1; sample[3][1][6]=0; sample[3][1][7]=1; sample[3][1][8]=0;
 	sample[3][2][0]=0; sample[3][2][1]=0; sample[3][2][2]=0; sample[3][2][3]=1; sample[3][2][4]=1; sample[3][2][5]=1; sample[3][2][6]=0; sample[3][2][7]=1; sample[3][2][8]=0;
 	sample[3][3][0]=0; sample[3][3][1]=1; sample[3][3][2]=0; sample[3][3][3]=1; sample[3][3][4]=1; sample[3][3][5]=0; sample[3][3][6]=0; sample[3][3][7]=1; sample[3][3][8]=0;

 	sample[4][0][0]=0; sample[4][0][1]=1; sample[4][0][2]=0; sample[4][0][3]=0; sample[4][0][4]=1; sample[4][0][5]=0; sample[4][0][6]=0; sample[4][0][7]=1; sample[4][0][8]=0;
 	sample[4][1][0]=0; sample[4][1][1]=0; sample[4][1][2]=0; sample[4][1][3]=1; sample[4][1][4]=1; sample[4][1][5]=1; sample[4][1][6]=0; sample[4][1][7]=0; sample[4][1][8]=0;
 	sample[4][2][0]=0; sample[4][2][1]=1; sample[4][2][2]=0; sample[4][2][3]=0; sample[4][2][4]=1; sample[4][2][5]=0; sample[4][2][6]=0; sample[4][2][7]=1; sample[4][2][8]=0;
 	sample[4][3][0]=0; sample[4][3][1]=0; sample[4][3][2]=0; sample[4][3][3]=1; sample[4][3][4]=1; sample[4][3][5]=1; sample[4][3][6]=0; sample[4][3][7]=0; sample[4][3][8]=0;







   if (thisNeighborhood.n[DOWN] == VACANT && thisNeighborhood.n[EAST] == VACANT) {

          setColor(RED);
          position[0]=127;
          position[1]=127;

          for (uint8_t p=0; p<6; p++) {
            if (thisNeighborhood.n[p] != VACANT) {
               sendCoordChunk(p);
                 nbreWaitedAnswers++;
             }
         }
     }



 while(1) {
         delayMS(100);

   }
}


/******************/
/**** systeme ****/
/******************/

void userRegistration(void) {
    registerHandler(SYSTEM_MAIN, (GenericHandler)&myMain);
}

void freeMyChunk(void) {
  freeChunk(thisChunk);
}


// find a useable chunk
Chunk* getFreeUserChunk(void) {
    Chunk* c;
    int i;

    for(i=0; i<MYCHUNKS; i++) {
        c = &(myChunks[i]);
        if(!chunkInUse(c)) {
            return c;
        }
    }
    return NULL;
}


/***********************/
/** FONCTION EMISSION**/
/**********************/


//***Distribution des coordonnées au voisin***//
byte sendCoordChunk(PRef p) {
      Chunk *c = getFreeUserChunk();

      if (c != NULL) {
          c->data[0]=position[0];
          c->data[1]=position[1];
          if (p==xplusBorder) {
              c->data[0]++;
          } else if (p==5-xplusBorder) {
              c->data[0]--;
          } else if (p==yplusBorder) {
              c->data[1]++;
          } else if (p==5-yplusBorder) {
              c->data[1]--;
          }
          if (sendMessageToPort(c, p, c->data, 2, coordMessageHandler,
 (GenericHandler)&freeMyChunk) == 0) {
              freeChunk(c);
              return 0;
          }
      }
      return 1;
 }

//***Acknowledge***//
 byte sendBackChunk(PRef p) {
     Chunk *c=getFreeUserChunk();
     c->data[0]=position[0];
     c->data[1]=position[1];

     if (c != NULL) {
         if (sendMessageToPort(c, p, c->data, 2, backMessageHandler, &freeMyChunk) == 0) {
             freeChunk(c);
             return 0;
         }
     }
     return 1;
 }



/**************************/
/** FONCTIONS RECEPTION **/
/*************************/

 byte coordMessageHandler(void) {
      if (thisChunk == NULL) return 0;
      byte sender = faceNum(thisChunk);

      delayMS(100);


      //***Je reçois des coordonnées identiques aux miennes***//
      if (position[0] == thisChunk->data[0] && position[1] == thisChunk->data[1]){
        sendBackChunk(sender);
        return 1;
      }

      //***Je reçois pour la première fois mes coordonnées***//
      else if (position[0] == 0 && position[1] == 0) {
        position[0] = thisChunk->data[0];
        position[1] = thisChunk->data[1];
        lien = sender;
        setColor(GREEN);
         for (uint8_t p=0; p<6; p++) {
          if (p!=sender && thisNeighborhood.n[p] != VACANT) {
            sendCoordChunk(p);
            nbreWaitedAnswers++;
            }
        }
    }

      //***Je reçois des coordonnées meilleurs que mes coordonnées actuelles***//
      else if (position[1] < thisChunk->data[1] || (position[1] == thisChunk->data[1] && position[0] < thisChunk->data[0])) {


          position[0] = thisChunk->data[0];
          position[1] = thisChunk->data[1];
          lien = sender;
          setColor(BLUE);
          nbreWaitedAnswers=0;
          for (uint8_t p=0; p<6; p++) {
           if (p!=sender && thisNeighborhood.n[p] != VACANT) {

             sendCoordChunk(p);
             nbreWaitedAnswers++;
              }
           }
        }


        //***Je reçois des coordonnées moins bonne que mes coordonnées actuelles***//
        else if (position[0] < thisChunk->data[0] || position[1] < thisChunk->data[1]) {
          sendCoordChunk(sender);
          nbreWaitedAnswers++;
        }



      if (nbreWaitedAnswers==0 && lien != NO_LIEN){
          sendBackChunk(lien);
        }
        //printf("%d,(%d;%d)\n",(int)getGUID(),position[0],position[1]);

      return 1;
}




 byte backMessageHandler(void) {
   if (thisChunk==NULL) return 0;
   uint8_t sender=faceNum(thisChunk);

   delayMS(100);

   if ( (sender==xplusBorder && thisChunk->data[0] == (position[0]+1) && thisChunk->data[1] == position[1]) ||
        (sender==5-xplusBorder && thisChunk->data[0] == (position[0]-1) && thisChunk->data[1] == position[1]) ||
        (sender==yplusBorder && thisChunk->data[0] == position[0] && thisChunk->data[1] == (position[1]+1)) ||
        (sender==5-yplusBorder && thisChunk->data[0] == position[0] && thisChunk->data[1] == (position[1]-1)) ) {

   nbreWaitedAnswers--;

   if (nbreWaitedAnswers==0 && lien == NO_LIEN){
     setColor(YELLOW);
   }



   if (nbreWaitedAnswers==0 && lien != NO_LIEN) {

     setColor(AQUA);
     sendBackChunk(lien);

     if (thisNeighborhood.n[WEST] == VACANT && thisNeighborhood.n[DOWN] == VACANT ){
       delayMS(1000);

     uint8_t width = position[0]-126; // afin de se compter dans le caclul
     sendExecOn(EAST,127,127,width,147);
   }


     else if (thisNeighborhood.n[EAST] == VACANT && thisNeighborhood.n[UP] == VACANT ){
       delayMS(1000);

     uint8_t height = position[1]-126;
     sendExecOn(DOWN,127,127,height,148);
      }
    }
  }
   return 1;
 }


//*** +++ ***//


byte sendExecOn(PRef p, byte px, byte py, byte donnee, byte fonc) {
	Chunk *c = getFreeUserChunk();

    delayMS(100);

    if (c!=NULL) {

		c->data[0] = px;
		c->data[1] = py;
		c->data[2] = donnee;
		c->data[3] = fonc;



		if (sendMessageToPort(c, p, c->data, 4, execOn, (GenericHandler)&freeMyChunk) == 0) {
			freeChunk(c);
			return 0;
		}
	}
	return 1;
}



	byte execOn(void) {
			if (thisChunk==NULL) return 0;
			uint8_t receiver[2];
			receiver[0] = thisChunk->data[0];
			receiver[1] = thisChunk->data[1];
			uint8_t donnee = thisChunk->data[2];
			byte fonc = thisChunk->data[3];

      //printf("%d, (%d;%d), %d, %d\n",(int)getGUID(),receiver[0],receiver[1], donnee, fonc);

      delayMS(100);

			if (position[0] != receiver[0]){

				if (position[0] < receiver[0] && thisNeighborhood.n[WEST] != VACANT)
					sendExecOn(WEST, receiver[0], receiver[1], donnee, fonc);

				else if (position[0] > receiver[0] && thisNeighborhood.n[EAST] != VACANT)
					sendExecOn(EAST, receiver[0], receiver[1], donnee, fonc);
			}


			else if (position[1] != receiver[1]){

				if (position[1] < receiver[1] && thisNeighborhood.n[UP] != VACANT)
					sendExecOn(UP, receiver[0], receiver[1], donnee, fonc);

				else if (position[1] > receiver[1] && thisNeighborhood.n[DOWN] != VACANT)
					sendExecOn(DOWN, receiver[0], receiver[1], donnee, fonc);
			}

      else if (position[0] == receiver[0] && position[1] == receiver[1]){
				if (fonc == 147)
          getSpawn(donnee,1);
        if (fonc == 148)
          getSpawn(donnee,2);

      if (fonc == 166)
        Spawn();

}

			return 1;
}

byte getSpawn(uint8_t donnee, uint8_t t){

  //printf("%d, %s\n", (int)getGUID(), "rentrée dans fct1");

  if (t == 1){
    tab[0] = (donnee/2);
    countspawn++;
  }
  else if (t == 2){
    tab[1] = donnee;
    countspawn++;
  }



    if (countspawn == 2){
    delayMS(100);
      sendExecOn(WEST,127+tab[0],124+tab[1],0,166);
      return 0;
  }
  return 1;

}


byte Spawn(void){


  fpos = 7;
  forme = 3;
  rota = 1;

  if (sample[forme][rota][fpos] == 1)
    setColor(YELLOW);
  else
    setColor(WHITE);

      for (uint8_t p=0; p<6; p++) {
          if (thisNeighborhood.n[p] != VACANT) {
              sendShape(p);
          }
      }



  return 0;
}

byte sendShape(PRef p) {
      Chunk *c = getFreeUserChunk();

      //if (c != NULL) {
          c->data[0]=forme;
          c->data[1]=rota;
          c->data[2]=fpos;
          if (p==xplusBorder && (fpos != 2 && fpos != 5 && fpos != 8))
              c->data[2]++;
          else if (p==5-xplusBorder && (fpos != 0 && fpos != 3 && fpos != 6))
              c->data[2]--;
          else if (p==yplusBorder)
              c->data[2]= c->data[2] - 3;
          else if (p==5-yplusBorder)
              c->data[2]= c->data[2] + 3;

          /*if ((c->data[2] <= 8 && c->data[2] >= 0) && (c->data[2] != fpos)){
						printf("%d, %d, %d, %s\n",(int)getGUID(), fpos, p, "Fonc envoi");*/


          if (sendMessageToPort(c, p, c->data, 3, shapeMessageHandler,
 (GenericHandler)&freeMyChunk) == 0) {
              freeChunk(c);
              return 0;
          }
      }
    }
      return 1;
 }

 byte shapeMessageHandler(void) {
   printf("%d, %s\n",(int)getGUID(), "Je rentre dans la fo shap-mess");
      if (thisChunk == NULL) return 0;
      byte sender = faceNum(thisChunk);

      //delayMS(2000);

      if (fpos==UNKNOWN){

        forme = thisChunk->data[0];
        rota = thisChunk->data[1];
        fpos = thisChunk->data[2];

        if (sample[forme][rota][fpos] == 1)
          setColor(YELLOW);
				else
					setColor(WHITE);
          printf("%d, %s\n",(int)getGUID(), "Light it up");


        for (uint8_t p=0; p<6; p++) {

          if (p!=sender && thisNeighborhood.n[p] != VACANT){

            sendShape(p);
            printf("%d, %d, %d, %s\n",(int)getGUID(), fpos, p, "Sent");
                    }
              }

              goingDown();

           //printf("%d, %s\n",(int)getGUID(), "All sent");


           //printf("%d, %d, %s\n",(int)getGUID(), fpos, "Je fais mon delay while");
}
      return 1;
}

byte goingDown(void){
  while (fpos <= 8 && fpos >= 0){

    if (sample[forme][rota][fpos] == 1)
      setColor(YELLOW);
    else
      setColor(WHITE);

      delayMS(5000);

  if (fpos == 0 || fpos == 1 || fpos == 2){
    setColor(AQUA);
    fpos = UNKNOWN;
  }

  else if (fpos == 6 || fpos == 7 || fpos == 8){
    fpos = fpos - 3;
    if (thisNeighborhood.n[DOWN] != VACANT)
     sendShape(DOWN);
   }

   else if (fpos == 3 || fpos == 4 || fpos == 5){
     fpos = fpos - 3;
   }
 }
   return 1;
}
