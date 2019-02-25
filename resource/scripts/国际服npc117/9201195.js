var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (status == 0 && mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
    if (status == 0) {
        var selStr = "?ú°•dæòæ£¤•Õ#v4000038#5üR?û·?? :\r\n\r\n#b#L0#¼ã¿m#v1662002#?ıc¾Jı~?³º’Ë(ùí)#l\r\n#L1#¼ã¿m#v1662003#?ıcúË¾Jı~?³º’Ë(’î)#l\r\n #L2#¼ã¿m#v1662004#ºMú¢¾Jı~?³º’Ë(ùí)#l\r\n#L3#¼ã¿m#v1662005#ºMú¢¾Jı~?³º’Ë(’î)#l\r\n#L4#¼ã¿m#v1662009#Á¡’ËÅÑ¾Jı~?³º’Ë(ùí)#l\r\n#L5#¼ã¿m#v1662010#Á¡’ËÅÑ¾Jı~?³º’Ë(’î)#l\r\n#L6#¼ã¿m#v1662006#?úY?“h#l\r\n#L7#¼ã¿m?³º’Ë??#l";
          cm.sendSimple(selStr);
    } else if (status == 1) {
        switch (selection) {
        case 0:
if (cm.haveItem(4000038 ,5) == true) {
cm.gainItem(4000038 ,-5);
    cm.gainItem(1662002,1);
     cm.sendOk("¼ã¿m£K“¢.");
            cm.dispose();
}else{
                cm.sendOk("#b”ªæ£“£¬a¡é£V#v4000038#?û·?ı~­Ë”ª¯Íæ");
                cm.dispose();
            }
            break;
        case 1:
if (cm.haveItem(4000038 ,5) == true) {
cm.gainItem(4000038 ,-5);
    cm.gainItem(1662003,1);
     cm.sendOk("¼ã¿m£K“¢.");
            cm.dispose();
}else{
                cm.sendOk("#b”ªæ£“£¬a¡é£V#v4000038#?û·?ı~­Ë”ª¯Íæ");
                cm.dispose();
            }
            break;
        case 2:
if (cm.haveItem(4000038 ,5) == true) {
cm.gainItem(4000038 ,-5);
    cm.gainItem(1662004,1);
     cm.sendOk("¼ã¿m£K“¢.");
            cm.dispose();
}else{
                cm.sendOk("#b”ªæ£“£¬a¡é£V#v4000038#?û·?ı~­Ë”ª¯Íæ");
                cm.dispose();
            }
            break;
        case 3:
if (cm.haveItem(4000038 ,5) == true) {
cm.gainItem(4000038 ,-5);
    cm.gainItem(1662005,1);
     cm.sendOk("¼ã¿m£K“¢.");
            cm.dispose();
}else{
                cm.sendOk("#b”ªæ£“£¬a¡é£V#v4000038#?û·?ı~­Ë”ª¯Íæ");
                cm.dispose();
            }
            break;
        case 4:
if (cm.haveItem(4000038 ,5) == true) {
cm.gainItem(4000038 ,-5);
    cm.gainItem(1662009,1);
     cm.sendOk("¼ã¿m£K“¢.");
            cm.dispose();
}else{
                cm.sendOk("#b”ªæ£“£¬a¡é£V#v4000038#?û·?ı~­Ë”ª¯Íæ");
                cm.dispose();
            }
            break;
        case 5:
if (cm.haveItem(4000038 ,5) == true) {
cm.gainItem(4000038 ,-5);
    cm.gainItem(1662010,1);
     cm.sendOk("¼ã¿m£K“¢.");
            cm.dispose();
}else{
                cm.sendOk("#b”ªæ£“£¬a¡é£V#v4000038#?û·?ı~­Ë”ª¯Íæ");
                cm.dispose();
            }
            break;
        case 6:
if (cm.haveItem(4000038 ,5) == true) {
cm.gainItem(4000038 ,-5);
    cm.gainItem(1662006,1);
     cm.sendOk("¼ã¿m£K“¢.");
            cm.dispose();
}else{
                cm.sendOk("#b”ªæ£“£¬a¡é£V#v4000038#?û·?ı~­Ë”ª¯Íæ");
                cm.dispose();
            }
            break;
        case 7:
if (cm.haveItem(4000038 ,5) == true) {
cm.gainItem(4000038 ,-5);
cm.gainItem(1672016,1);
     cm.sendOk("¼ã¿m£KndOk("ÁÊ¶R¦¨¥\.");
            cm.dispose();
}else{
                cm.sendOk("#b§A­I¥]¸Ì¨S¦³#v4000038#¼ú¬×¤£¯àµ¹§A³á¡I");
                cm.dispose();
            }
            break;
        }
    }
}
