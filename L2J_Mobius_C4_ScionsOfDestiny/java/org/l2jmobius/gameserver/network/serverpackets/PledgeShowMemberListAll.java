/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2jmobius.gameserver.network.serverpackets;

import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.clan.Clan;
import org.l2jmobius.gameserver.model.clan.ClanMember;
import org.l2jmobius.gameserver.network.ServerPackets;

public class PledgeShowMemberListAll extends ServerPacket
{
	private final Clan _clan;
	private final Player _player;
	
	public PledgeShowMemberListAll(Clan clan, Player player)
	{
		_clan = clan;
		_player = player;
	}
	
	@Override
	public void write()
	{
		ServerPackets.PLEDGE_SHOW_MEMBER_LIST_ALL.writeId(this);
		writeInt(_clan.getClanId());
		writeString(_clan.getName());
		writeString(_clan.getLeaderName());
		writeInt(_clan.getCrestId()); // crest id .. is used again
		writeInt(_clan.getLevel());
		writeInt(_clan.getCastleId());
		writeInt(_clan.getHideoutId());
		writeInt(0);
		writeInt(_player.getLevel()); // ??
		writeInt(_clan.getDissolvingExpiryTime() > System.currentTimeMillis() ? 3 : 0);
		writeInt(0);
		writeInt(_clan.getAllyId());
		writeString(_clan.getAllyName());
		writeInt(_clan.getAllyCrestId());
		writeInt(_clan.isAtWar()); // new c3
		writeInt(_clan.getMembers().size() - 1);
		for (ClanMember m : _clan.getMembers())
		{
			// On C4 player is not shown.
			if (m.getObjectId() == _player.getObjectId())
			{
				continue;
			}
			writeString(m.getName());
			writeInt(m.getLevel());
			writeInt(m.getClassId());
			writeInt(0);
			writeInt(1);
			writeInt(m.isOnline() ? m.getObjectId() : 0); // 1=online 0=offline
		}
	}
}
