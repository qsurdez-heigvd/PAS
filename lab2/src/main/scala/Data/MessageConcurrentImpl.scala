package Data

import Chat.ExprTree
import Data.MessageService.{MsgContent, Username}
import scalatags.Text.Frag

/**
  * Wrapper arround the original MessageService implementation that prevents
  * issues when updating a variable shared by multiple threads.
  * 
  * This is done through the use of "synchronized" (same as Java).
  *
  * @param messageImpl the original implementation
  */
class MessageConcurrentImpl(messageImpl: MessageImpl) extends MessageService:
  override def add(
      sender: Username,
      msg: MsgContent,
      mention: Option[Username] = None,
      exprType: Option[ExprTree] = None,
      replyToId: Option[Long] = None,
  ): Long =
    synchronized { messageImpl.add(sender, msg, mention, exprType, replyToId) }

  override def getLatestMessages(n: Int) =
    synchronized { messageImpl.getLatestMessages(n) }

  override def deleteHistory(): Unit =
    synchronized { messageImpl.deleteHistory() }
