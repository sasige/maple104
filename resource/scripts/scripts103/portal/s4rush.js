function enter(pi) {
    if (pi.getQuestStatus(6110) == 1) {
	 if (pi.getParty() != null) {
	     if (!pi.isLeader()) {
		 pi.playerMessage("只有组队长才能决定进入。" );
	     } else {
		 if (pi.getParty().getMembers().size < 2) {
		    pi.playerMessage("你的组队成员不足两人。" );
		 } else {
		      if (!pi.isAllPartyMembersAllowedJob(1)) {
			  pi.playerMessage("你不能进去，你的组队员不是战士。");
		      } else {
			  var em = pi.getEventManager("4jrush");
			  if (em == null) {
			      pi.playerMessage("由于未知的原因，不能进入。 );
			  } else {
			      em.startInstance(pi.getParty(), pi.getMap());
			      return true;
			  }
		      }
		 }
	     }
	 } else {
	     pi.playerMessage(5, "请组队后在来。");
	 }
    } else {
	pi.playerMessage("未知的力量阻挡着你的前进。");
    }
    return false;
}