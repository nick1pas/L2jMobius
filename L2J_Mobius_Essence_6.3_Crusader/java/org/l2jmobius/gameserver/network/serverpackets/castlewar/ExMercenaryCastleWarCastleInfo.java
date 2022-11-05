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
package org.l2jmobius.gameserver.network.serverpackets.castlewar;

import java.util.Calendar;

import org.l2jmobius.gameserver.enums.TaxType;
import org.l2jmobius.gameserver.model.siege.Castle;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Serenitty
 */
public class ExMercenaryCastleWarCastleInfo extends ServerPacket
{
	private final Castle _castle;
	
	public ExMercenaryCastleWarCastleInfo(Castle castle)
	{
		_castle = castle;
	}
	
	@Override
	public void write()
	{
		ServerPackets.EX_MERCENARY_CASTLEWAR_CASTLE_INFO.writeId(this);
		writeInt(_castle.getResidenceId());
		final var clan = _castle.getOwner();
		if (clan != null)
		{
			writeInt(clan.getId());
			writeInt(clan.getCrestId());
			writeSizedString(clan.getName());
			writeSizedString(clan.getLeaderName());
		}
		else
		{
			writeInt(0);
			writeInt(0);
			writeSizedString("");
			writeSizedString("");
		}
		writeInt(_castle.getTaxPercent(TaxType.BUY));
		writeLong((long) (_castle.getTreasury() * _castle.getTaxRate(TaxType.BUY)));
		writeLong((long) (_castle.getTreasury() + (_castle.getTreasury() * _castle.getTaxRate(TaxType.BUY))));
		final Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(_castle.getSiegeDate().getTimeInMillis());
		writeInt((int) (cal.getTimeInMillis() / 1000));
	}
}
