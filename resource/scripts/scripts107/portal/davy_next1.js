function enter(pi) {
    try {
        var em = pi.getEventManager("Pirate");
        if (em != null && em.getProperty("stage2").equals("3")) {
            pi.warp(925100200, 0); //next
        } else {
            pi.playerMessage(5, "门还是关闭的，请通过NPC开启下一道门");
        }
    } catch(e) {
        pi.playerMessage(5, "Error: " + e);
    }
}