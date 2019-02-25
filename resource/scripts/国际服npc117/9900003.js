var items = [1003507, 1032137, 1052444, 1098000, 1098001, 1098002, 1098003, 1112723, 1142399, 1142400, 1142401, 1142402, 1142403, 1302214, 3010360];

function start() { 
    cm.sendSimple("\r\n#L0#£gúÁ??ù@!");
}

 
function action(mode, type, selection) {
    for (var i = 0; i < items.length; i++) {
        if (!cm.canHold(items[i], 1)) {
            var error = "It seems that you can't hold the items, please make sure you have enough inventory space.";
            //TODO: make a variable for one of a kind items.
            var oneofakind = cm.itemQuantity(1032137) > 0 || cm.itemQuantity(1142399) > 0 || cm.itemQuantity(1142400) > 0 || cm.itemQuantity(1142401) > 0 || cm.itemQuantity(1142402) > 0 || cm.itemQuantity(1142403) > 0 ? "\r\nAlso, it seems that you have one of the next items, get rid of them:" : "";
            oneofakind += cm.itemQuantity(1032137) > 0 ? "\r\n#b#i1032137##z1032137##k" : "";
            oneofakind += cm.itemQuantity(1142399) > 0 ? "\r\n#b#i1142399##z1142399##k" : "";
            oneofakind += cm.itemQuantity(1142400) > 0 ? "\r\n#b#i1142400##z1142400##k" : "";
            oneofakind += cm.itemQuantity(1142401) > 0 ? "\r\n#b#i1142401##z1142401##k" : "";
            oneofakind += cm.itemQuantity(1142402) > 0 ? "\r\n#b#i1142402##z1142402##k" : "";
            oneofakind += cm.itemQuantity(1142403) > 0 ? "\r\n#b#i1142403##z1142403##k" : "";
            cm.sendOk(error + oneofakind);
            cm.dispose();
            return;
        }
    }
    for (var item = 0; item < items.length; item++)
        cm.gainItem(items[item], 1);
    cm.setHair(36033);
    cm.setFace(20169);
    cm.dispose());
}