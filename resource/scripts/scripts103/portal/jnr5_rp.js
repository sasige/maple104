function enter(pi) {
	//if (pi.getPlayer().getMap().getReactorByName("jnr3_out1").getState() > 0) {
		var rand = Math.floor(Math.random() * 30);
		var portal;
		if (rand == 0){
					portal = "pt10";
		}else if (rand == 1){
					portal = "pt02";
		}else if (rand == 2){
					portal = "pt21"
		}else if (rand == 3){
					portal = "pt30"
		}else if (rand == 4){
					portal = "pt40"
		}else if (rand == 5){
					portal = "pt50"
		}else if (rand == 6){
					portal = "pt60"
		}else if (rand == 7){
					portal = "pt70"
		}else if (rand == 8){
					portal = "pt80"
		}else if (rand == 9){
					portal = "pt90"
		}else if (rand == 10){
					portal = "pt10"
		}else{
					portal = "success"
		}
               pi.warp(926110301,portal);
                return true;
      //  }
       // else {
         //       pi.playerMessage("门被锁着，需要芯片钥匙才能打开。");
           //     return false;
        //}
}