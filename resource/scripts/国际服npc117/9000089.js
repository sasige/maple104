var status = -1;

function action(mode, type, selection) {
    cm.dispose();
    cm.openNpc(Math.floor(cm.getMapId() / 10000000) == 26 ? 9000090 : Math.floor(cm.getMapId() / 100000000) == 1 ? 1012000 : 1012000);
}