#include "block.bbh"
#include "clock.bbh"
//#include "coordinates_propagation"

#ifdef COORD_PROPAGATION


threaddef  #define NO_LIEN  99

threadvar byte positionOnGrid[2];
threadvar byte SpawnXY[2];

threadvar  uint8_t DataLink;
threadvar  uint8_t NbreAnswersStillWaited;
threadvar byte OnXPositiveBorder;
threadvar byte OnyPositiveBorder;
threadvar uint8_t spawncounter;
threadvar uint8_t isSpawn;


threaddef  #define MYCHUNKS 12
threadextern Chunk* thisChunk;
threadvar  Chunk foundChunks[MYCHUNKS];

 /***************/
 /** functions **/
 byte goMessageHandler(void);
 byte sendBackChunk(PRef p);
 byte backMessageHandler(void);
 byte sendCoordChunk(PRef p);
 byte coordMessageHandler(void);
 byte sendExecOn(PRef p, byte px, byte py, byte donnee, byte fonc);
 byte execOn(void);
 byte Itwork(void);
 byte setSpawn(void);
 byte getSpawn(uint8_t donnee,uint8_t t);


/******************************/


 void Core(void) {

   if (thisNeighborhood.n[DOWN] == VACANT && thisNeighborhood.n[EAST] == VACANT) {

          setColor(RED);
          positionOnGrid[0]=127;
          positionOnGrid[1]=127;

          for (uint8_t p=0; p<6; p++) {
            if (thisNeighborhood.n[p] != VACANT) {
               sendCoordChunk(p);
                 NbreAnswersStillWaited++;
             }
         }
     }


/*
 while(1) {
         delayMS(100);
         //if (positionOnGrid[0] == 127 && positionOnGrid[1] == 127 && NbreAnswersStillWaited == 0)
          //setColor(YELLOW);


   }
   */
}


/******************/
/**** systeme ****/
/******************/

void initCoord(void) {
  delayMS(50);
  DataLink=NO_LIEN;
  NbreAnswersStillWaited=0;
  positionOnGrid[0] = 0;
  positionOnGrid[1] = 0;
  OnXPositiveBorder = WEST;
  OnyPositiveBorder = UP;
  SpawnXY[0]= 0;
  SpawnXY[1]= 0;
  spawncounter=0;
    Core();
}

void freeMyChunk(void) {
  freeChunk(thisChunk);
}


// find a useable chunk
Chunk* getFreeUserChunk(void) {
    Chunk* c;
    int i;

    for(i=0; i<MYCHUNKS; i++) {
        c = &(foundChunks[i]);
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
          c->data[0]=positionOnGrid[0];
          c->data[1]=positionOnGrid[1];
          if (p==OnXPositiveBorder) {
              c->data[0]++;
          } else if (p==5-OnXPositiveBorder) {
              c->data[0]--;
          } else if (p==OnyPositiveBorder) {
              c->data[1]++;
          } else if (p==5-OnyPositiveBorder) {
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
     c->data[0]=positionOnGrid[0];
     c->data[1]=positionOnGrid[1];

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
      if (positionOnGrid[0] == thisChunk->data[0] && positionOnGrid[1] == thisChunk->data[1]){
        sendBackChunk(sender);
        return 1;
      }

      //***Je reçois pour la première fois mes coordonnées***//
      else if (positionOnGrid[0] == 0 && positionOnGrid[1] == 0) {
        positionOnGrid[0] = thisChunk->data[0];
        positionOnGrid[1] = thisChunk->data[1];
        DataLink = sender;
        setColor(GREEN);
         for (uint8_t p=0; p<6; p++) {
          if (p!=sender && thisNeighborhood.n[p] != VACANT) {
            sendCoordChunk(p);
            NbreAnswersStillWaited++;
            }
        }
    }

      //***Je reçois des coordonnées meilleurs que mes coordonnées actuelles***//
      else if (positionOnGrid[1] < thisChunk->data[1] || (positionOnGrid[1] == thisChunk->data[1] && positionOnGrid[0] < thisChunk->data[0])) {


          positionOnGrid[0] = thisChunk->data[0];
          positionOnGrid[1] = thisChunk->data[1];
          DataLink = sender;
          setColor(BLUE);
          NbreAnswersStillWaited=0;
          for (uint8_t p=0; p<6; p++) {
           if (p!=sender && thisNeighborhood.n[p] != VACANT) {

             sendCoordChunk(p);
             NbreAnswersStillWaited++;
              }
           }
        }


        //***Je reçois des coordonnées moins bonne que mes coordonnées actuelles***//
        else if (positionOnGrid[0] < thisChunk->data[0] || positionOnGrid[1] < thisChunk->data[1]) {
          sendCoordChunk(sender);
          NbreAnswersStillWaited++;
        }

        delayMS(100);


      if (NbreAnswersStillWaited==0 && DataLink != NO_LIEN){
          sendBackChunk(DataLink);
        }
        //printf("%d,(%d;%d)\n",(int)getGUID(),positionOnGrid[0],positionOnGrid[1]);

      /*  if (getGUID() == 2 || getGUID() == 5 || getGUID() == 31 || getGUID() == 35)
        printf("%d,(%d;%d)\n",(int)getGUID(),positionOnGrid[0],positionOnGrid[1]);*/

      return 1;
}




 byte backMessageHandler(void) {
   if (thisChunk==NULL) return 0;
   uint8_t sender=faceNum(thisChunk);

   delayMS(100);

   if ( (sender==OnXPositiveBorder && thisChunk->data[0] == (positionOnGrid[0]+1) && thisChunk->data[1] == positionOnGrid[1]) ||
        (sender==5-OnXPositiveBorder && thisChunk->data[0] == (positionOnGrid[0]-1) && thisChunk->data[1] == positionOnGrid[1]) ||
        (sender==OnyPositiveBorder && thisChunk->data[0] == positionOnGrid[0] && thisChunk->data[1] == (positionOnGrid[1]+1)) ||
        (sender==5-OnyPositiveBorder && thisChunk->data[0] == positionOnGrid[0] && thisChunk->data[1] == (positionOnGrid[1]-1)) ) {

   NbreAnswersStillWaited--;
   //printf("%d, Reponses = %d, Envoyeur: %d\n",(int)getGUID(), NbreAnswersStillWaited, sender);

   if (NbreAnswersStillWaited==0 && DataLink == NO_LIEN){
     setColor(YELLOW);
   }



   if (NbreAnswersStillWaited==0 && DataLink != NO_LIEN) {

       setColor(AQUA);
       sendBackChunk(DataLink);


       if (thisNeighborhood.n[WEST] == VACANT && thisNeighborhood.n[DOWN] == VACANT ){
	delayMS(1000);


       uint8_t width = positionOnGrid[0]-126; // afin de se compter dans le caclul
       sendExecOn(EAST,127,127,width,147);
       //printf("%s : %d  \n","largeur envoyée",width);
       //setColor(ORANGE);
     }


       else if (thisNeighborhood.n[EAST] == VACANT && thisNeighborhood.n[UP] == VACANT ){
         delayMS(1000);

       uint8_t height = positionOnGrid[1]-126;
       sendExecOn(DOWN,127,127,height,148);
       //printf("%s : %d  \n","Hauteur envoyée",height);
       //setColor(ORANGE);
        }
      }
    }

   return 1;
 }

//*** +++ ***//


byte sendExecOn(PRef p, byte px, byte py, byte donnee, byte fonc) {
	Chunk *c = getFreeUserChunk();

    delayMS(50);

    if (c!=NULL) {

		c->data[0] = px;
		c->data[1] = py;
		c->data[2] = donnee;
		c->data[3] = fonc;


    //printf("%d, (%d, %d, %d, %d)\n", (int)getGUID(), c->data[0], c->data[1], c->data[2], c->data[3]);


		if (sendMessageToPort(c, p, c->data, 4, execOn, (GenericHandler)&freeMyChunk) == 0) {
      //printf("%d, %s\n", (int)getGUID(), "sendExecOn");
			freeChunk(c);
			return 0;
		}
	}
	return 1;
}



	byte execOn(void) {
			if (thisChunk==NULL) return 0;
			uint8_t sender = faceNum(thisChunk);
			uint8_t receiver[2];
			receiver[0] = thisChunk->data[0];
			receiver[1] = thisChunk->data[1];
			uint8_t donnee = thisChunk->data[2];
			byte fonc = thisChunk->data[3];

      //printf("%d, (%d;%d) \n",(int)getGUID(),positionOnGrid[0],positionOnGrid[1]);

      //setColor(RED);
      delayMS(50);

			//c->data[0] = px;
			//c->data[1] = py;
			//c->data[2] = donnee;
			//c->data[3] = fonc;

			if (positionOnGrid[0] != receiver[0]){

				if (positionOnGrid[0] < receiver[0])
					sendExecOn(WEST, receiver[0], receiver[1], donnee, fonc);

				else
					sendExecOn(EAST, receiver[0], receiver[1], donnee, fonc);
			}


			else if (positionOnGrid[1] != receiver[1]){

				if (positionOnGrid[1] < receiver[1])
					sendExecOn(UP, receiver[0], receiver[1], donnee, fonc);

				else
					sendExecOn(DOWN, receiver[0], receiver[1], donnee, fonc);
			}

			else if (positionOnGrid[0] == receiver[0] && positionOnGrid[1] == receiver[1]){
				if (fonc == 147)
          getSpawn(donnee,1);
        if (fonc == 148)
          getSpawn(donnee,2);

      if (fonc == 166)
        setSpawn();
}

			return 1;
}

byte getSpawn(uint8_t donnee, uint8_t t){

//printf("%s : %d \n","execution de getspawn avc data : ", donnee );

  if (t == 1){
    SpawnXY[0] = (donnee/2);
    spawncounter++;
    //printf("%s : %d  \n","COORD X RECUES : " ,127+SpawnXY[0]);
  }
  else if (t == 2){
    SpawnXY[1] = donnee;
    spawncounter++;
    //printf("%s : %d \n","COORD Y RECUES : " ,127+SpawnXY[1]);
  }



    if (spawncounter == 2){
    delayMS(200);
      sendExecOn(WEST,127+SpawnXY[0],126+SpawnXY[1],0,166);
      return 0;
  }
  return 1;

}


byte setSpawn(void){
//while (1) {
    setColor(WHITE);

    isSpawn = 1;
    triggerHandler(COORDINATE_COMPUTED);

////    setColor(PURPLE);
//    delayMS(150);

/* THE GAME BEGINS */
//}


  return 0;
}

#endif
