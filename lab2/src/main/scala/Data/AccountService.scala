package Data

import scala.annotation.tailrec
import scala.collection.concurrent.TrieMap
import scala.collection.mutable

trait AccountService:
  /** Retrieve the balance of a given account
    * @param user
    *   the name of the user whose account will be retrieve
    * @return
    *   the current balance of the user
    */
  def getAccountBalance(user: String): Double

  /** Add an account to the existing accounts
    * @param user
    *   the name of the user
    * @param balance
    *   the initial balance value
    */
  def addAccount(user: String, balance: Double): Unit

  /** Indicate is an account exist
    * @param user
    *   the name of the user whose account is checked to exist
    * @return
    *   whether the account exists or not
    */
  def isAccountExisting(user: String): Boolean

  /** Update an account by decreasing its balance.
    * @param user
    *   the name of the user whose account will be updated
    * @param amount
    *   the amount to decrease
    * @return
    *   the new balance if the purchase succeeded or None otherwise
    */
  def purchase(user: String, amount: Double): Option[Double]

  /** Set the current user, creating its account if it doesn't exist and setting
    * the current user for the session.
    *
    * @param user
    *   the new current user
    * @param session
    *   the session on which the new user will be set
    */
  def setCurrent(user: String, session: Session): Unit

class AccountImpl extends AccountService:

  private val accounts: TrieMap[String, Double] = TrieMap.empty

  override def getAccountBalance(user: String): Double = {
    // No need to be using getOrElse as we already check if
    // the account exists
    if isAccountExisting(user) then accounts(user)
    else throw new Exception(s"No account for user: $user")
  }

  override def addAccount(user: String, balance: Double): Unit =
    if !isAccountExisting(user) then accounts.addOne(user, balance)

  override def isAccountExisting(user: String): Boolean =
    accounts.contains(user)

  override def purchase(user: String, amount: Double): Option[Double] =
    @tailrec
    def attempt(): Option[Double] =
      accounts.get(user) match
        case Some(balance) if balance >= amount =>
          val newBalance = balance - amount
          if accounts.replace(
              user,
              balance,
              newBalance,
            ) // Returns true only if the old value is still the same
          then Some(newBalance)
          else attempt() // Another thread updated it, retry
        case _ => None

    attempt()

  override def setCurrent(user: String, session: Session): Unit =
    if !isAccountExisting(user) then addAccount(user, 30)
    session.setCurrentUser(user)
end AccountImpl
