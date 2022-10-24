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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.l2jmobius.Config;
import org.l2jmobius.commons.network.PacketWriter;
import org.l2jmobius.gameserver.enums.TaxType;
import org.l2jmobius.gameserver.instancemanager.CastleManager;
import org.l2jmobius.gameserver.model.actor.Player;
import org.l2jmobius.gameserver.model.actor.Summon;
import org.l2jmobius.gameserver.model.buylist.Product;
import org.l2jmobius.gameserver.model.buylist.ProductList;
import org.l2jmobius.gameserver.model.item.instance.Item;
import org.l2jmobius.gameserver.network.OutgoingPackets;
import org.l2jmobius.gameserver.network.PacketLogger;

/**
 * @author ShanSoft, Index
 */
public class ExBuySellList extends AbstractItemPacket
{
	public static final int BUY_SELL_LIST_BUY = 0;
	public static final int BUY_SELL_LIST_SELL = 1;
	public static final int BUY_SELL_LIST_UNK = 2;
	public static final int BUY_SELL_LIST_TAX = 3;
	
	public static final int UNK_SELECT_FIRST_TAB = 0;
	public static final int UNK_SHOW_PURCHASE_LIST = 1;
	public static final int UNK_SEND_NOT_ENOUGH_ADENA_MESSAGE = 2;
	public static final int UNK_SEND_INCORRECT_ITEM_MESSAGE = 3;
	
	private static final int[] CASTLES =
	{
		3, // Giran
		7, // Goddart
		5, // Aden
	};
	
	private final int _inventorySlots;
	private final int _type;
	
	// buy type - BUY
	private long _money;
	private double _castleTaxRate;
	private Collection<Product> _list;
	private int _listId;
	
	// buy type - SELL
	private final List<Item> _sellList = new ArrayList<>();
	private final Collection<Item> _refundList = new ArrayList<>();
	private boolean _done;
	
	// buy type = unk
	private int _unkType;
	
	// buy type - send tax
	private boolean _applyTax;
	
	public ExBuySellList(ProductList list, Player player, double castleTaxRate)
	{
		_type = BUY_SELL_LIST_BUY;
		_listId = list.getListId();
		_list = list.getProducts();
		_money = player.getAdena();
		_inventorySlots = player.getInventory().getNonQuestSize();
		_castleTaxRate = castleTaxRate;
	}
	
	public ExBuySellList(Player player, boolean done)
	{
		_type = BUY_SELL_LIST_SELL;
		final Summon pet = player.getPet();
		for (Item item : player.getInventory().getItems())
		{
			if (!item.isEquipped() && item.isSellable() && ((pet == null) || (item.getObjectId() != pet.getControlObjectId())))
			{
				_sellList.add(item);
			}
		}
		_inventorySlots = player.getInventory().getNonQuestSize();
		if (player.hasRefund())
		{
			_refundList.addAll(player.getRefund().getItems());
		}
		_done = done;
	}
	
	public ExBuySellList(int type)
	{
		_type = BUY_SELL_LIST_UNK;
		_unkType = type;
		_inventorySlots = 0;
	}
	
	public ExBuySellList(boolean applyTax)
	{
		_type = BUY_SELL_LIST_TAX;
		_inventorySlots = 0;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_BUY_SELL_LIST.writeId(packet);
		packet.writeD(_type);
		switch (_type)
		{
			case BUY_SELL_LIST_BUY:
			{
				return sendBuyList(packet);
			}
			case BUY_SELL_LIST_SELL:
			{
				return sendSellList(packet);
			}
			case BUY_SELL_LIST_UNK:
			{
				return sendUnk(packet);
			}
			case BUY_SELL_LIST_TAX:
			{
				return sendCurrentTax(packet);
			}
			default:
			{
				PacketLogger.warning(getClass().getSimpleName() + ": unknown type " + _type);
				return false;
			}
		}
	}
	
	private boolean sendBuyList(PacketWriter packet)
	{
		packet.writeQ(_money); // current money
		packet.writeD(_listId);
		packet.writeD(_inventorySlots);
		packet.writeH(_list.size());
		for (Product product : _list)
		{
			if ((product.getCount() > 0) || !product.hasLimitedStock())
			{
				writeItem(packet, product);
				packet.writeQ((long) (product.getPrice() * (1.0 + _castleTaxRate + product.getBaseTaxRate())));
			}
		}
		return true;
	}
	
	private boolean sendSellList(PacketWriter packet)
	{
		packet.writeD(_inventorySlots);
		if (!_sellList.isEmpty())
		{
			packet.writeH(_sellList.size());
			for (Item item : _sellList)
			{
				writeItem(packet, item);
				packet.writeQ(Config.MERCHANT_ZERO_SELL_PRICE ? 0 : item.getTemplate().getReferencePrice() / 2);
			}
		}
		else
		{
			packet.writeH(0);
		}
		if (!_refundList.isEmpty())
		{
			packet.writeH(_refundList.size());
			int i = 0;
			for (Item item : _refundList)
			{
				writeItem(packet, item);
				packet.writeD(i++);
				packet.writeQ(Config.MERCHANT_ZERO_SELL_PRICE ? 0 : (item.getTemplate().getReferencePrice() / 2) * item.getCount());
			}
		}
		else
		{
			packet.writeH(0);
		}
		packet.writeC(_done ? 1 : 0);
		return true;
	}
	
	private boolean sendUnk(PacketWriter packet)
	{
		packet.writeC(_unkType);
		return true;
	}
	
	private boolean sendCurrentTax(PacketWriter packet)
	{
		packet.writeD(CASTLES.length);
		for (int id : CASTLES)
		{
			packet.writeD(id); // residence id
			try
			{
				packet.writeD(_applyTax ? CastleManager.getInstance().getCastleById(id).getTaxPercent(TaxType.BUY) : 0); // residence tax
			}
			catch (NullPointerException ignored)
			{
				packet.writeD(0);
			}
		}
		return true;
	}
}
