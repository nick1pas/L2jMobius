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
import org.l2jmobius.gameserver.model.clan.ClanMember;
import org.l2jmobius.gameserver.network.ServerPackets;

/**
 * @author -Wooden-
 */
public class PledgeReceiveMemberInfo extends ServerPacket
{
	private final ClanMember _member;
	private final Player _player;
	
	public PledgeReceiveMemberInfo(ClanMember member, Player player)
	{
		_member = member;
		_player = player;
	}
	
	@Override
	public void write()
	{
		ServerPackets.PLEDGE_RECEIVE_MEMBER_INFO.writeId(this);
		writeInt(_member.getClan().getClanId());
		writeString(_member.getClan().getName());
		writeString(_member.getClan().getLeaderName());
		writeInt(_member.getClan().getCrestId()); // crest id .. is used again
		writeInt(_member.getClan().getLevel());
		writeInt(_member.getClan().getCastleId());
		writeInt(_member.getClan().getHideoutId());
		writeInt(0);
		writeInt(_player.getLevel()); // ??
		writeInt(_member.getClan().getDissolvingExpiryTime() > System.currentTimeMillis() ? 3 : 0);
		writeInt(0);
		writeInt(_member.getClan().getAllyId());
		writeString(_member.getClan().getAllyName());
		writeInt(_member.getClan().getAllyCrestId());
		writeInt(_member.getClan().isAtWar()); // new c3
		writeInt(_member.getClan().getMembers().size() - 1);
		for (ClanMember m : _member.getClan().getMembers())
		{
			// TODO is this c4?
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
