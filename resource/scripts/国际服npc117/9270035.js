var name = "#b#e²|²|“dûÖver.117#k#n"; 
var talk = "”ª´fğE?©Î?¥z¬Ş?\r\n\r\n"; 
var menu = ["?’ë","ÛUÚu?","“A??","??","üû?","?¬ø£©?","ıC?","¥°?ÖÍöï","¬øÚyúk"]; 
var options = [ 
/*Warrior*/   ["âì’ï","’Ú§ı","’Ø§ı","??","??","û¾?","ºğ","¦ä?–J","??–J","¦ä?âã","??âã","¦ä??","???","í½","?"],  
/*Mage*/      ["âì’ï","??","??","û¾","ºğ","Òè•½","ú¹•½"],  
/*Archer*/    ["âì’ï","??","??","ºğ","“A","?","??"],  
/*Thief*/     ["âì’ï","’Ú§ı","’Ø§ı","??","??","û¾?","ºğ","Òè’Ğ","ü°?","ûö?"],  
/*Pirate*/    ["âì’ï","??","??","ºğ","Úy³º","•W´A"],
/*Phantom*/   ["Úu•½","“ª"],
/*Cannoneer*/ ["•WıC"],
/*Mercedes*/  ["Úy³º","ÛUÚu?"],
/*db*/        ["?’Ğ"]];
var colors = ["#g","#r","#d","#b"]; 
var rand = Math.floor(Math.random()*4); 
var rand2 = Math.ceil(Math.floor(Math.random()*4)); 
var c; 
npc = 0; 
function start() { 
    var text = "úÁ? #e#d#h ##k#n. ?ú°£¤•Õ "+name+". "+talk+""; 
    for (var z = 0; z < menu.length; z++) 
        text+= "#L"+z+"##e"+colors[rand]+""+menu[z]+"#l\r\n"; 
    cm.sendSimple(text); 
} 
function action(m,t,s) { 
    if (m != 1) { 
        cm.dispose(); 
        return; 
    }else{ 
        npc++; 
    } 
    if (npc == 1) { 
        c = s; 
        for (var i = 0; i < options[c].length; i++) 
            talk+="#L"+i+"##e"+colors[rand2]+""+options[c][i]+"#k#l\r\n"; 
        cm.sendSimple(talk); 
    } else if (npc == 2) { 
        cm.openShop(6100+((c*100)+s)); 
        cm.dispose(); 
    } 
}  