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

import org.l2jmobius.commons.network.PacketWriter;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.network.OutgoingPackets;

public class AskJoinPledge implements IClientOutgoingPacket
{
	private final Player _requestor;
	private final String _pledgeName;
	
	public AskJoinPledge(Player requestor, String pledgeName)
	{
		_requestor = requestor;
		_pledgeName = pledgeName;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.ASK_JOIN_PLEDGE.writeId(packet);
		packet.writeD(_requestor.getObjectId());
		packet.writeS("");
		packet.writeS(_pledgeName);
		packet.writeD(0);
		packet.writeS("");
		return true;
	}
}
