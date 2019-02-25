var status = 0;

function start() {
    cm.sendSimple ("#r§J?úY #h # ?”UŞ?Öv?û·”²?NPCà\r\n#g??Íg?’Ú£V  #v4000038# #c4000038#üR?û·à\r\n\r\n#b#L1#?ğE?5£eí·??£K5üR?û·\r\n#b#L2#[´f?§¿?]?ğE??û·?£K80í·?");
    }

function action(mode, type, selection) {
        cm.dispose();

    switch(selection){ 
        case 1:
            if (cm.getMeso() >= 5000000) {
            cm.gainMeso(-5000000);
            cm.gainItem(4000038 ,5);
            cm.sendOk("#b’ú?£O’Ø5£eí·?Ş¥s?ÓIæ£“£Öv”È£V£O•Õ#v4000038#?û·’Éà");
                cm.dispose();
            }else{
                cm.sendOk("#b”ªúYú¸Ñû?iŞ¥s?ÓIà");
                cm.dispose();
            }
                break;
İİİİİİİİcase 2:
            if (cm.haveItem(4000038 ,1) == true) {
            cm.gainMeso(800000);
            cm.gainItem(4000038 ,-1);
            cm.sendOk("#b’ú?£O’Ø#v4000038#?û·Ş¥s?ÓIæ£“£Öv”È£V£O•Õ80í·?à");
                cm.dispose();
            }else{
                cm.sendOk("#b”ªæ£“£¬a¡é£V#v4000038#?û·?ı~­Ë”ª?¯Íæ");
                cm.dispose();
            }

            }
        }