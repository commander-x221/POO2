package sociald1.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import sociald1.NewsFeed;
import sociald1.Post;
import sociald1.User;

import static sociald1.test.DataProvider.LG_STREAM;
import static sociald1.test.DataProvider.postSupplier;
import static sociald1.test.DataProvider.stringSupplier;
import static sociald1.test.DataProvider.userSupplier;
import static sociald1.test.DataProvider.newsFeedSupplier;

/**
 * Test class for NewsFeed.
 *
 * Fil d'actualité d'un utilisateur du réseau social Microdon.
 * 
 * Un NewsFeed regroupe les Post d'un User et ceux de tous les User auxquels il
 * est abonné. Il permet de naviguer dans la liste de tous ces Post qui est
 * ordonnée du plus récent au plus ancien. Cette navigation doit être initiée
 * par un appel à la méthode startIteration() ou au constructeur, ensuite,
 * l'appel à la méthode next() permet d'aller vers les messages plus anciens et
 * l'appel à la méthode previous() permet d'aller vers les Post plus récents.
 * 
 * Chaque itération utilise les méthodes d'itération des User concernés par ce
 * NewsFeed. Lorsqu'une itération a été initié par un appel à la méthode
 * startIteration() ou au constructeur, aucun des utilisateurs ne doit poster de
 * nouveaux messages. Si cette condition n'est pas respectée les résultats
 * obtenus sont imprévisibles. Cependant, un nouvel appel à startIteration()
 * permet de rétablir un fonctionnement normal pour une nouvelle itération.
 */
public class TestNewsFeed {

	public static Stream<NewsFeed> newsFeedProvider() {
		// Put here the code to return a Stream of instances of NewsFeed:
		return Stream.generate(() -> newsFeedSupplier()).limit(LG_STREAM);
	}
	
	public static Stream<User> userProvider() {
		return Stream.generate(() -> userSupplier()).limit(LG_STREAM);
	}

	private User mainUser;
	private User lastUser;
	private int postNb;
	private int nextIndex;
	private int previousIndex;
	private int lastIndex;

	private void saveState(NewsFeed self) {
		// Put here the code to save the state of self:
		this.mainUser = self.getUser();
		this.lastUser = self.lastUser();
		this.postNb = self.size();
		this.nextIndex = self.nextIndex();
		this.previousIndex = self.previousIndex();
		this.lastIndex = self.lastIndex();
	}

	private void assertPurity(NewsFeed self) {
		// Put here the code to check purity for self:
		assertEquals(mainUser, self.getUser());
		assertEquals(lastUser, self.lastUser());
		assertEquals(postNb, self.size());
		assertEquals(nextIndex, self.nextIndex());
		assertEquals(previousIndex, self.previousIndex());
		assertEquals(lastIndex, self.lastIndex());
	}

	public void assertInvariant(NewsFeed self) {
		// Put here the code to check the invariant:
		// @invariant getUser() != null;
		assertNotNull(self.getUser());
		// @invariant size() == getUser().getPostNb() + (\sum User u; getUser().getSubscriptions().contains(u); u.getPostNb());
		int size = self.getUser().getPostNb();
		for (User u : self.getUser().getSubscriptions()) {
			size += u.getPostNb();
		}
		assertEquals(size, self.size());

		// @invariant nextIndex() == previousIndex() + 1;
		assertEquals(self.previousIndex() + 1, self.nextIndex());
		// @invariant lastIndex() == nextIndex() || lastIndex() == previousIndex();
		assertTrue((self.lastIndex() == self.nextIndex()) || (self.lastIndex() == self.previousIndex()));
		// @invariant previousIndex() >= -1 && previousIndex() < size();
		assertTrue(self.previousIndex() >= -1 && self.previousIndex() < self.size());
		// @invariant nextIndex() >= 0 && nextIndex() <= size();
		assertTrue(self.nextIndex() >= 0 && self.nextIndex() <= self.size());
		// @invariant lastIndex() >= -1 && lastIndex() < size();
		assertTrue(self.lastIndex() >= -1 && self.lastIndex() < self.size());
		// @invariant lastUser() == null || lastUser().equals(getUser()) || getUser().getSubscriptions().contains(lastUser());
		assertTrue(self.lastUser() == null || self.lastUser().equals(self.getUser()) || self.getUser().getSubscriptions().contains(self.lastUser()));
		// @invariant lastIndex() == -1 <==> lastUser() == null;
		assertEquals(self.lastIndex() == -1, self.lastUser() == null);
		// @invariant !hasPrevious() <==> previousIndex() == -1;
		assertEquals(!self.hasPrevious(), self.previousIndex() == -1);
		// @invariant !hasNext() <==> nextIndex() == size();
		assertEquals(!self.hasNext(), self.nextIndex() == self.size());
	}

	/**
	 * Test method for constructor NewsFeed
	 *
	 * Initialise un nouveau NewsFeed pour l'utilisateur spécifié. Le NewsFeed est
	 * initialisé de manière à pouvoir commencer une itération de la même manière
	 * que si la méthode startIteration avait été appelée.
	 */
	@ParameterizedTest
	@MethodSource("userProvider")
	public void testNewsFeed(User aUser) {

		// Pré-conditions:
		// @requires aUser != null;
		assumeTrue(aUser != null);

		// Oldies:

		// Exécution:
		NewsFeed result = new NewsFeed(aUser);

		// Post-conditions:
		// @ensures getUser().equals(aUser);
		assertEquals(aUser, result.getUser());
		// @ensures !hasPrevious();
		assertFalse(result.hasPrevious());
		// @ensures previousIndex() == -1;
		assertTrue(result.previousIndex() == -1);
		// @ensures hasNext() <==> size() > 0;
		assertEquals(result.size() > 0, result.hasNext());
		// @ensures nextIndex() == 0;
		assertTrue(result.nextIndex() == 0);
		// @ensures lastIndex() == -1;
		assertTrue(result.lastIndex() == -1);
		// @ensures lastUser() == null;
		assertNull(result.lastUser());

		// Invariant:
		assertInvariant(result);
	}

	/**
	 * Test method for method getUser
	 *
	 * Renvoie l'utilisateur dont ce NewsFeed est le fil d'actualité.
	 */
	@ParameterizedTest
	@MethodSource("newsFeedProvider")
	public void testgetUser(NewsFeed self) {
		assumeTrue(self != null);

		// Invariant:
		assertInvariant(self);

		// Pré-conditions:

		// Save state for purity check:
		saveState(self);

		// Oldies:

		// Exécution:
		User result = self.getUser();

		// Post-conditions:

		// Assert purity:
		assertPurity(self);

		// Invariant:
		assertInvariant(self);
	}

	/**
	 * Test method for method startIteration
	 *
	 * Démarre une nouvelle itération de ce NewsFeed.
	 */
	@ParameterizedTest
	@MethodSource("newsFeedProvider")
	public void teststartIteration(NewsFeed self) {
		assumeTrue(self != null);

		// Invariant:
		assertInvariant(self);

		// Pré-conditions:

		// Oldies:

		// Exécution:
		self.startIteration();

		// Post-conditions:
		// @ensures !hasPrevious();
		assertFalse(self.hasPrevious());
		// @ensures previousIndex() == -1;
		assertTrue(self.previousIndex() == -1);
		// @ensures hasNext() <==> size() > 0;
		assertEquals(self.size() > 0, self.hasNext());
		// @ensures nextIndex() == 0;
		assertTrue(self.nextIndex() == 0);
		// @ensures lastIndex() == -1;
		assertTrue(self.lastIndex() == -1);
		// @ensures lastUser() == null;
		assertNull(self.lastUser());
		// @ensures !getUser().hasPrevious();
		assertFalse(self.getUser().hasPrevious());
		// @ensures (\forall User u; getUser().equals(u) || getUser().getSubscriptions().contains(u); !u.hasPrevious());
		for (User u : self.getUser().getSubscriptions()) {
			assertFalse(u.hasPrevious());
		}
		// @ensures (\forall User u; getUser().equals(u) || getUser().getSubscriptions().contains(u); u.lastIndex() == -1);
		assertTrue(self.getUser().lastIndex() == -1);
		for (User u : self.getUser().getSubscriptions()) {
			assertTrue(u.lastIndex() == -1);
		}

		// Invariant:
		assertInvariant(self);
	}

	/**
	 * Test method for method hasPrevious
	 *
	 * Renvoie true si et seulement si cette itération possède un Post précédent
	 * (i.e. plus récent).
	 */
	@ParameterizedTest
	@MethodSource("newsFeedProvider")
	public void testhasPrevious(NewsFeed self) {
		assumeTrue(self != null);

		// Invariant:
		assertInvariant(self);

		// Pré-conditions:

		// Save state for purity check:
		saveState(self);

		// Oldies:

		// Exécution:
		boolean result = self.hasPrevious();

		// Post-conditions:
		// @ensures \result <==> (\exists User u; getUser().equals(u) || getUser().getSubscriptions().contains(u); u.hasPrevious());
		boolean found = self.getUser().hasPrevious();
		Iterator<User> iter = self.getUser().getSubscriptions().iterator();
		while (iter.hasNext() && !found ) {
			found = iter.next().hasPrevious();
		}
		assertEquals(found, result);
		
		// Assert purity:
		assertPurity(self);

		// Invariant:
		assertInvariant(self);
	}

	/**
	 * Test method for method previousIndex
	 *
	 * Renvoie, s'il en existe un, l'index du Post précédent dans cette itération.
	 * Renvoie -1 si cette itération ne possède pas de Post précédent.
	 */
	@ParameterizedTest
	@MethodSource("newsFeedProvider")
	public void testpreviousIndex(NewsFeed self) {
		assumeTrue(self != null);

		// Invariant:
		assertInvariant(self);

		// Pré-conditions:

		// Save state for purity check:
		saveState(self);

		// Oldies:

		// Exécution:
		int result = self.previousIndex();

		// Post-conditions:

		// Assert purity:
		assertPurity(self);

		// Invariant:
		assertInvariant(self);
	}

	/**
	 * Test method for method previous
	 *
	 * Renvoie le Post précédent (plus récent) de cette itération et recule d'un
	 * élement. Un appel postérieur à next() renverra le même élement.
	 */
	@ParameterizedTest
	@MethodSource("newsFeedProvider")
	public void testprevious(NewsFeed self) {
		assumeTrue(self != null);

		// Invariant:
		assertInvariant(self);

		// Pré-conditions:
		// @requires hasPrevious();
		assumeTrue(self.hasPrevious());

		// Oldies:
		// old in:@ensures previousIndex() == \old(previousIndex()) - 1;
		int oldPrevIndex = self.previousIndex();
		// old in:@ensures nextIndex() == \old(nextIndex()) - 1;
		// old in:@ensures lastIndex() == \old(previousIndex());
		int oldNextIndex = self.nextIndex();

		// Exécution:
		Post result = self.previous();

		// Post-conditions:
		// @ensures \result != null;
		assertNotNull(result);
		// @ensures previousIndex() == \old(previousIndex()) - 1;
		assertEquals(oldPrevIndex - 1, self.previousIndex());
		// @ensures nextIndex() == \old(nextIndex()) - 1;
		assertEquals(oldNextIndex - 1, self.nextIndex());
		// @ensures lastIndex() == \old(previousIndex());
		assertEquals(oldPrevIndex, self.lastIndex());
		// @ensures lastUser() != null;
		assertNotNull(self.lastUser());
		// @ensures \result.equals(lastUser().getPost(lastUser().lastIndex()));
		User lastUser = self.lastUser();
		assertEquals(lastUser.getPost(lastUser.lastIndex()), result);

		// Invariant:
		assertInvariant(self);
	}

	/**
	 * Test method for method hasNext
	 *
	 * Renvoie true si et seulement si cette itération possède un Post postérieur
	 * (i.e. plus ancien).
	 */
	@ParameterizedTest
	@MethodSource("newsFeedProvider")
	public void testhasNext(NewsFeed self) {
		assumeTrue(self != null);

		// Invariant:
		assertInvariant(self);

		// Pré-conditions:

		// Save state for purity check:
		saveState(self);

		// Oldies:

		// Exécution:
		boolean result = self.hasNext();

		// Post-conditions:
		// @ensures \result <==> (\exists User u; getUser().equals(u) || getUser().getSubscriptions().contains(u); u.hasNext());
		boolean found = self.getUser().hasNext();
		Iterator<User> iter = self.getUser().getSubscriptions().iterator();
		while (iter.hasNext() && !found) {
			found = iter.next().hasNext();
		}
		assertEquals(found, result);


		// Assert purity:
		assertPurity(self);

		// Invariant:
		assertInvariant(self);
	}

	/**
	 * Test method for method next
	 *
	 * Renvoie le Post suivant (plus ancien) de cette itération et avance d'un
	 * élement dans l'itération. Un appel ultérieur à prévious() renverra le même
	 * élement.
	 */
	@ParameterizedTest
	@MethodSource("newsFeedProvider")
	public void testnext(NewsFeed self) {
		assumeTrue(self != null);

		// Invariant:
		assertInvariant(self);

		// Pré-conditions:
		// @requires hasNext();
		assumeTrue(self.hasNext());

		// Oldies:
		// old in:@ensures previousIndex() == \old(previousIndex()) + 1;
		int oldPrevIndex = self.previousIndex();
		// old in:@ensures nextIndex() == \old(nextIndex()) + 1;
		// old in:@ensures lastIndex() == \old(nextIndex());
		int oldNextIndex = self.nextIndex();

		// Exécution:
		Post result = self.next();

		// Post-conditions:
		// @ensures \result != null;
		assertNotNull(result);
		// @ensures previousIndex() == \old(previousIndex()) + 1;
		assertEquals(oldPrevIndex + 1, self.previousIndex());
		// @ensures nextIndex() == \old(nextIndex()) + 1;
		assertEquals(oldNextIndex + 1, self.nextIndex());
		// @ensures lastIndex() == \old(nextIndex());
		assertEquals(oldNextIndex, self.lastIndex());
		// @ensures lastUser() != null;
		assertNotNull(self.lastUser());
		// @ensures \result.equals(lastUser().getPost(lastUser().lastIndex()));
		User lastUser =self.lastUser();
		assertEquals(lastUser.getPost(lastUser.lastIndex()), result);

		// Invariant:
		assertInvariant(self);
	}

	/**
	 * Test method for method nextIndex
	 *
	 * Renvoie, s'il en existe un, l'index du Post suivant dans cette itération.
	 * Renvoie -1 si cette itération ne possède pas de Post suivant.
	 */
	@ParameterizedTest
	@MethodSource("newsFeedProvider")
	public void testnextIndex(NewsFeed self) {
		assumeTrue(self != null);

		// Invariant:
		assertInvariant(self);

		// Pré-conditions:

		// Save state for purity check:
		saveState(self);

		// Oldies:

		// Exécution:
		int result = self.nextIndex();

		// Post-conditions:

		// Assert purity:
		assertPurity(self);

		// Invariant:
		assertInvariant(self);
	}

	/**
	 * Test method for method lastIndex
	 *
	 * Renvoie l'index dans ce NewsFeed de l'élement renvoyé par le dernier appel à
	 * next() ou previous(). Renvoie -1 si aucun appel à next() ou prévious() n'a
	 * été effectué depuis le démarrage de cette itération (i.e. depuis la création
	 * de l'instance ou le dernier appel à startIteration()).
	 */
	@ParameterizedTest
	@MethodSource("newsFeedProvider")
	public void testlastIndex(NewsFeed self) {
		assumeTrue(self != null);

		// Invariant:
		assertInvariant(self);

		// Pré-conditions:

		// Save state for purity check:
		saveState(self);

		// Oldies:

		// Exécution:
		int result = self.lastIndex();

		// Post-conditions:

		// Assert purity:
		assertPurity(self);

		// Invariant:
		assertInvariant(self);
	}

	/**
	 * Test method for method lastUser
	 *
	 * Renvoie l'auteur du Post renvoyé par le dernier appel à next() ou previous().
	 * Renvoie null si aucun appel à next() ou prévious() n'a été effectué depuis le
	 * démarrage de cette itération (i.e. depuis la création de l'instance ou le
	 * dernier appel à startIteration()).
	 */
	@ParameterizedTest
	@MethodSource("newsFeedProvider")
	public void testlastUser(NewsFeed self) {
		assumeTrue(self != null);

		// Invariant:
		assertInvariant(self);

		// Pré-conditions:

		// Save state for purity check:
		saveState(self);

		// Oldies:

		// Exécution:
		User result = self.lastUser();

		// Post-conditions:
		// @ensures \result != null ==> getUser().getSubscriptions().contains(\result);
		if (result != null) {
			assertTrue(self.getUser().getSubscriptions().contains(result));
		}

		// Assert purity:
		assertPurity(self);

		// Invariant:
		assertInvariant(self);
	}

	/**
	 * Test method for method size
	 *
	 * Renvoie le nombre total de Post de ce NewsFeed.
	 */
	@ParameterizedTest
	@MethodSource("newsFeedProvider")
	public void testsize(NewsFeed self) {
		assumeTrue(self != null);

		// Invariant:
		assertInvariant(self);

		// Pré-conditions:

		// Save state for purity check:
		saveState(self);

		// Oldies:

		// Exécution:
		int result = self.size();

		// Post-conditions:

		// Assert purity:
		assertPurity(self);

		// Invariant:
		assertInvariant(self);
	}
} // End of the test class for NewsFeed
