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
package org.l2jmobius.gameserver.network.serverpackets.limitshop;

import java.util.List;

import org.l2jmobius.gameserver.data.xml.LimitShopCraftData;
import org.l2jmobius.gameserver.data.xml.LimitShopData;
import org.l2jmobius.gameserver.model.holders.LimitShopProductHolder;
import org.l2jmobius.gameserver.model.holders.LimitShopRandomCraftReward;
import org.l2jmobius.gameserver.network.ServerPackets;
import org.l2jmobius.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Gustavo Fonseca
 */
public class ExPurchaseLimitShopItemResult extends ServerPacket
{
	private final int _category, _productId;
	private final boolean _isSuccess;
	private final List<LimitShopRandomCraftReward> _rewards;
	private final LimitShopProductHolder _product;
	
	public ExPurchaseLimitShopItemResult(boolean isSuccess, int category, int productId, List<LimitShopRandomCraftReward> rewards)
	{
		_isSuccess = isSuccess;
		_category = category;
		_productId = productId;
		_rewards = rewards;
		switch (_category)
		{
			case 3: // Normal Lcoin Shop
			{
				_product = LimitShopData.getInstance().getProduct(_productId);
				break;
			}
			case 4: // Lcoin Special Craft
			{
				_product = LimitShopCraftData.getInstance().getProduct(_productId);
				break;
			}
			default:
			{
				_product = null;
			}
		}
	}
	
	@Override
	public void write()
	{
		ServerPackets.EX_PURCHASE_LIMIT_SHOP_ITEM_BUY.writeId(this);
		if ((_product == null) || !_isSuccess)
		{
			writeByte(1);
			writeByte(_category);
			writeInt(_productId);
			writeInt(1);
			writeByte(1);
			writeInt(0);
			writeLong(0);
		}
		else
		{
			writeByte(0); // success
			writeByte(_category);
			writeInt(_productId);
			writeInt(_rewards.size());
			int counter = 0;
			for (LimitShopRandomCraftReward entry : _rewards)
			{
				if (counter == _rewards.size())
				{
					break;
				}
				writeByte(entry.getRewardIndex());
				writeInt(0);
				writeInt(entry.getCount());
				counter++;
			}
		}
	}
}