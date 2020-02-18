package knight.gsp;

import knight.gsp.account.Account;

/***
 * 处理玩家发过来的协议接口
 */
public interface IProtocolHandle {
    void Handle(Account account);
}
