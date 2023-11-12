package sociald1.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static sociald1.test.DataProvider.LG_STREAM;
import static sociald1.test.DataProvider.postSupplier;
import static sociald1.test.DataProvider.stringSupplier;
import static sociald1.test.DataProvider.userSupplier;
import static sociald1.test.DataProvider.randInt;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import sociald1.Post;
import sociald1.User;


/**
 * Test class for User.
 *
 * Un utilisateur du réseau social Microdon.
 *
 * Un User est caractérisé par:
 * <ul>
 * <li>son nom et son mot de passe</li>
 * <li>sa date d'inscription sur Microdon (= date de création de l'instance de
 * l'User)</li>
 * <li>l'ensemble des utilisateurs auxquels il est abonné et dont les Post sont
 * intégrés à son fil d'actualité</li>
 * <li>la liste de ses Post ordonnés du plus récent au plus ancien</li>
 * </ul>
 *
 * Un ensemble complet de méthodes permet d'effectuer des itérations
 * bidirectionnelles sur la liste des Post de l'User. Ce sont les méthodes:
 * startIteration(), hasNext(), nextIndex(), next(), hasPrevious(),
 * previousIndex(), previous() et lastIndex().
 *
 * <pre>{@code
 * aUser.startIteration(); // Initialisation pour une nouvelle itération
 * // Affichage des Post du plus récent au plus ancien
 * while (aUser.hasNext()) {
 * 	System.out.println("Post suivant (plus ancien):" + aUser.next());
 * }
 * // Affichage des Post du plus ancien au plus récent
 * while (aUser.hasPrevious()) {
 * 	System.out.println("Post suivant (plus ancien):" + aUser.previous());
 * }
 * }</pre>
 */
public class TestUser {

	public static Stream<Arguments> stringAndStringProvider() {
		return Stream.generate(() -> Arguments.of(stringSupplier(), stringSupplier())).limit(LG_STREAM);
	}

	public static Stream<User> userProvider() {
		return Stream.generate(() -> userSupplier()).limit(LG_STREAM);
	}

	public static Stream<Arguments> userAndUserProvider() {
		return Stream.generate(() -> Arguments.of(userSupplier(), userSupplier())).limit(LG_STREAM);
	}

	public static Stream<Arguments> userAndStringProvider() {
		return Stream.generate(() -> Arguments.of(userSupplier(), stringSupplier())).limit(LG_STREAM);
	}

	public static Stream<Arguments> userAndIntProvider() {
		return userProvider().map(u -> Arguments.of(u, randInt(u.getPostNb())));
	}

	private String name;
	private String password;
	private Instant registrationDate;
	private Set<User> subscriptions;
	private List<Post> userPosts;
	private int previousIndex;
	private int nextIndex;
	private int lastIndex;
	private Post lastPost;

	private void saveState(User self) {
		// Put here the code to save the state of self:
		name = self.getName();
		password = self.getPassword();
		registrationDate = self.getRegistrationDate();
		subscriptions = new HashSet<User>(self.getSubscriptions());
		userPosts = new ArrayList<Post>(self.getPosts());
		previousIndex = self.previousIndex();
		nextIndex = self.nextIndex();
		lastIndex = self.lastIndex();
		if (lastIndex >= 0) {
			lastPost = self.getPost(lastIndex);
		} else {
			lastPost = null;
		}
	}

	private void assertPurity(User self) {
		// Put here the code to check purity for self:
		assertEquals(name, self.getName());
		assertEquals(password, self.getPassword());
		assertEquals(registrationDate, self.getRegistrationDate());
		assertEquals(subscriptions, self.getSubscriptions());
		assertEquals(userPosts, self.getPosts());
		assertEquals(previousIndex, self.previousIndex());
		assertEquals(nextIndex, self.nextIndex());
		assertEquals(lastIndex, self.lastIndex());
		if (self.lastIndex() != -1) {
			assertEquals(lastPost, self.getPost(self.lastIndex()));
		}
	}

	public void assertInvariant(User self) {
		// Put here the code to check the invariant:
		// @invariant getName() != null && !getName().isBlank();
		assertNotNull(self.getName());
		assertFalse(self.getName().isBlank());
		// @invariant getPassword() != null && !getPassword().isBlank();
		assertNotNull(self.getPassword());
		assertFalse(self.getPassword().isBlank());
		// @invariant getRegistrationDate() != null;
		assertNotNull(self.getRegistrationDate());
		// @invariant getSubscriptions() != null && !getSubscriptions().contains(null);
		assertNotNull(self.getSubscriptions());
		assertFalse(self.getSubscriptions().contains(null));
		// @invariant getSubscriptionNb() == getSubscriptions().size();
		assertEquals(self.getSubscriptionNb(), self.getSubscriptions().size());
		// @invariant getPosts() != null && !getPosts().contains(null);
		assertNotNull(self.getPosts());
		assertFalse(self.getPosts().contains(null));
		// @invariant (\forall int i, j; i >= 0 && i < j && j < getPostNb(); getPost(i).isAfter(getPost(j)));
		for (int i = 0; i < (self.getPostNb() - 1); i++) {
			assertTrue(self.getPost(i).isAfter(self.getPost(i + 1)));
		}
		// @invariant getPostNb() == getPosts().size();
		assertTrue(self.getPostNb() == self.getPosts().size());
		// @invariant previousIndex() >= -1 && previousIndex() < getPosts().size();
		assertTrue(self.previousIndex() >= -1);
		assertTrue(self.previousIndex() < self.getPosts().size());
		// @invariant nextIndex() >= 0 && nextIndex() <= getPosts().size();
		assertTrue(self.nextIndex() >= 0);
		assertTrue(self.nextIndex() <= self.getPosts().size());
		// @invariant nextIndex() == previousIndex() + 1;
		assertTrue(self.nextIndex() == self.previousIndex() + 1);
		// @invariant lastIndex() >= -1 && lastIndex() < getPosts().size();
		assertTrue(self.lastIndex() >= -1);
		assertTrue(self.lastIndex() < self.getPosts().size());
		// @invariant lastIndex() == previousIndex() || lastIndex() == nextIndex();
		assertTrue(self.lastIndex() == self.previousIndex() || self.lastIndex() == self.nextIndex());
	}

	/**
	 * Test method for constructor User
	 *
	 * Initialise une nouvelle instance ayant les nom et mot de passe spécifiés. La
	 * date d'inscription du nouvel utilisateur est la date au moment de l'exécution
	 * de ce constructeur.
	 */
	@ParameterizedTest
	@MethodSource("stringAndStringProvider")
	public void testUser(String userName, String password) {

		// Pré-conditions:
		// @requires userName != null && !userName.isBlank();
		assumeTrue(userName != null && !userName.isBlank());
		// @requires password != null && !password.isBlank();
		assumeTrue(password != null && !password.isBlank());

		// Oldies:
		Instant oldDate = Instant.now();
		// Wait until oldDate is really old:
		ArrayList<User> list = new ArrayList<User>(1);
		while (!oldDate.isBefore(Instant.now())) {
			list.add(null);
		}
		// Exécution:
		User result = new User(userName, password);

		// Wait until exec date is really old:
		Instant execDate = Instant.now();
		while (!execDate.isBefore(Instant.now())) {
			list.add(null);
		}
		list = null;
		// Post-conditions:
		// @ensures getName().equals(userName);
		assertEquals(userName, result.getName());
		// @ensures getPassword().equals(password);
		assertEquals(password, result.getPassword());
		// @ensures getRegistrationDate() != null;
		assertNotNull(result.getRegistrationDate());
		// @ensures oldDate.isBefore(getRegistrationDate());
		assertTrue(oldDate.isBefore(result.getRegistrationDate()));
		// @ensures getRegistrationDate().isBefore(Instant.now());
		assertTrue(result.getRegistrationDate().isBefore(Instant.now()));
		// @ensures getSubscriptions() != null;;
		assertNotNull(result.getSubscriptions());
		// @ensures getSubscriptions().isEmpty();
		assertTrue(result.getSubscriptions().isEmpty());
		// @ensures getPosts() != null;
		assertNotNull(result.getPosts());
		// @ensures getPosts().isEmpty();
		assertTrue(result.getPosts().isEmpty());
		// @ensures !hasPrevious();
		assertFalse(result.hasPrevious());
		// @ensures previousIndex() == -1;
		assertTrue(result.previousIndex() == -1);
		// @ensures nextIndex() == 0;
		assertTrue(result.nextIndex() == 0);
		// @ensures lastIndex() == -1;
		assertTrue(result.lastIndex() == -1);

		// Invariant:
		assertInvariant(result);
	}

	/**
	 * Test method for method getName
	 *
	 * Renvoie le nom de cet utilisateur.
	 */
	@ParameterizedTest
	@MethodSource("userProvider")
	public void testgetName(User self) {
		assumeTrue(self != null);

		// Invariant:
		assertInvariant(self);

		// Pré-conditions:

		// Save state for purity check:
		saveState(self);

		// Oldies:

		// Exécution:
		String result = self.getName();

		// Post-conditions:

		// Assert purity:
		assertPurity(self);

		// Invariant:
		assertInvariant(self);
	}

	/**
	 * Test method for method getPassword
	 *
	 * Renvoie le mot de passe de cet utilisateur.
	 */
	@ParameterizedTest
	@MethodSource("userProvider")
	public void testgetPassword(User self) {
		assumeTrue(self != null);

		// Invariant:
		assertInvariant(self);

		// Pré-conditions:

		// Save state for purity check:
		saveState(self);

		// Oldies:

		// Exécution:
		String result = self.getPassword();

		// Post-conditions:

		// Assert purity:
		assertPurity(self);

		// Invariant:
		assertInvariant(self);
	}

	/**
	 * Test method for method getRegistrationDate
	 *
	 * Renvoie l'Instant représentant la date d'inscription de cet utilisateur.
	 */
	@ParameterizedTest
	@MethodSource("userProvider")
	public void testgetRegistrationDate(User self) {
		assumeTrue(self != null);

		// Invariant:
		assertInvariant(self);

		// Pré-conditions:

		// Save state for purity check:
		saveState(self);

		// Oldies:

		// Exécution:
		Instant result = self.getRegistrationDate();

		// Post-conditions:

		// Assert purity:
		assertPurity(self);

		// Invariant:
		assertInvariant(self);
	}

	/**
	 * Test method for method getSubscriptions
	 *
	 * Renvoie une nouvele instance de HashSet contenant l'ensemble des utilisateurs
	 * auxquels cet utilisateur s'est abonné.
	 */
	@ParameterizedTest
	@MethodSource("userProvider")
	public void testgetSubscriptions(User self) {
		assumeTrue(self != null);

		// Invariant:
		assertInvariant(self);

		// Pré-conditions:

		// Save state for purity check:
		saveState(self);

		// Oldies:

		// Exécution:
		HashSet<User> result = self.getSubscriptions();

		// Post-conditions:
		// @ensures result != null;
		assertNotNull(result);
		// @ensures (\forall User u; \result.contains(u); hasSubscritionTo(u));
		for (User u : result) {
			self.hasSubscriptionTo(u);
		}
		// @ensures (\forall User u; hasSubscriptionTo(u); \result.contains(u));
		for (User u : DataProvider.allUser()) {
			if (self.hasSubscriptionTo(u)) {
				assertTrue(result.contains(u));
			}
		}
		// @ensures \result.size() == getSubscriptionNb();
		assertTrue(result.size() == self.getSubscriptionNb());

		// test de l'indépendance:
		assertTrue(result != self.getSubscriptions());
		result.clear();
		result.add(self);

		// Assert purity:
		assertPurity(self);

		// Invariant:
		assertInvariant(self);
	}

	/**
	 * Test method for method addSubscription
	 *
	 * Ajoute l'utilisateur spécifié à l'ensemble des souscriptions de cet
	 * utilisateur. Un utilisateur ne peut souscrire à lui-même. Renvoie true si
	 * l'utilisateur spécifié ne faisait pas déjà partie des souscriptions de cet
	 * utilisateur.
	 */
	@ParameterizedTest
	@MethodSource("userAndUserProvider")
	public void testaddSubscription(User self, User u) {
		assumeTrue(self != null);

		// Invariant:
		assertInvariant(self);

		// Pré-conditions:
		// @requires u != null;
		assumeTrue(u != null);
		// @requires !this.equals(u);
		assumeTrue(!self.equals(u));

		// Oldies:
		// old in:@ensures \result <==> !\old(hasSubscriptionTo(u));
		boolean oldHasSub = self.hasSubscriptionTo(u);
		// old in:@ensures \result <==> (getSubscriptionNb() == \old(getSubscriptionNb() + 1));
		// old in:@ensures !\result <==> (getSubscriptionNb() == \old(getSubscriptionNb()));
		int oldSubNb = self.getSubscriptionNb();
		// old in:@ensures !\result <==> (getSubscriptions().equals(\old(getSubscriptions()));
		Set<User> oldSubs = new HashSet<User>(self.getSubscriptions());

		// Exécution:
		boolean result = self.addSubscription(u);

		// Post-conditions:
		// @ensures hasSubscriptionTo(u);
		assertTrue(self.hasSubscriptionTo(u));
		// @ensures \result <==> !\old(hasSubscriptionTo(u));
		assertEquals(!oldHasSub, result);
		// @ensures \result <==> (getSubscriptionNb() == \old(getSubscriptionNb() + 1));
		assertEquals(self.getSubscriptionNb() == oldSubNb + 1, result);
		// @ensures !\result <==> (getSubscriptionNb() == \old(getSubscriptionNb()));
		assertEquals(self.getSubscriptionNb() == oldSubNb, !result);
		// @ensures !\result <==> (getSubscriptions().equals(\old(getSubscriptions()));
		assertEquals(self.getSubscriptions().equals(oldSubs), !result);
		// @ensures getSubscriptions().containsAll(oldSubs);
		assertTrue(self.getSubscriptions().containsAll(oldSubs));

		// Invariant:
		assertInvariant(self);
	}

	/**
	 * Test method for method removeSubscription
	 *
	 * Retire l'utilisateur spécifié de l'ensemble des abonnements de cet
	 * utilisateur. Renvoie true si l'utilisateur spécifié était précédemment un
	 * abonnement de cet utilisateur.
	 */
	@ParameterizedTest
	@MethodSource("userAndUserProvider")
	public void testremoveSubscription(User self, User u) {
		assumeTrue(self != null);

		// Invariant:
		assertInvariant(self);

		// Pré-conditions:

		// Oldies:
		// old in:@ensures \result <==> \old(hasSubscriptionTo(u));
		boolean oldHasSub = self.hasSubscriptionTo(u);
		// old in:@ensures \result <==> (getSubscriptionNb() == \old(getSubscriptionNb() - 1));
		// old in:@ensures !\result <==> (getSubscriptionNb() == \old(getSubscriptionNb()));
		int oldSubNb = self.getSubscriptionNb();
		// old in:@ensures !\result <==> (getSubscriptions().equals(\old(getSubscriptions()));
		Set<User> oldSubs = new HashSet<User>(self.getSubscriptions());

		// Exécution:
		boolean result = self.removeSubscription(u);

		// Post-conditions:
		// @ensures !hasSubscriptionTo(u);
		assertFalse(self.hasSubscriptionTo(u));
		// @ensures \result <==> \old(hasSubscriptionTo(u));
		assertEquals(oldHasSub, result);
		// @ensures \result <==> (getSubscriptionNb() == \old(getSubscriptionNb() - 1));
		assertEquals(self.getSubscriptionNb() == oldSubNb - 1, result);
		// @ensures !\result <==> (getSubscriptionNb() == \old(getSubscriptionNb()));
		assertEquals(self.getSubscriptionNb() == oldSubNb, !result);
		// @ensures !\result <==> (getSubscriptions().equals(\old(getSubscriptions()));
		assertEquals(self.getSubscriptions().equals(oldSubs), !result);

		// Invariant:
		assertInvariant(self);
	}

	/**
	 * Test method for method hasSubscriptionTo
	 *
	 * Renvoie true si l'utilisateur spécifié fait partie des abonnements de cet
	 * utilisateur.
	 */
	@ParameterizedTest
	@MethodSource("userAndUserProvider")
	public void testhasSubscriptionTo(User self, User u) {
		assumeTrue(self != null);

		// Invariant:
		assertInvariant(self);

		// Pré-conditions:

		// Save state for purity check:
		saveState(self);

		// Oldies:

		// Exécution:
		boolean result = self.hasSubscriptionTo(u);

		// Post-conditions:
		// @ensures \result <==> getSubscriptions().contains(u);
		assertEquals(self.getSubscriptions().contains(u), result);


		// Assert purity:
		assertPurity(self);

		// Invariant:
		assertInvariant(self);
	}

	/**
	 * Test method for method getSubscriptionNb
	 *
	 * Renvoie le nombre d'utilisateurs auxquels cet utilisateur est abonné.
	 */
	@ParameterizedTest
	@MethodSource("userProvider")
	public void testgetSubscriptionNb(User self) {
		assumeTrue(self != null);

		// Invariant:
		assertInvariant(self);

		// Pré-conditions:

		// Save state for purity check:
		saveState(self);

		// Oldies:

		// Exécution:
		int result = self.getSubscriptionNb();

		// Post-conditions:
		// @ensures \result == getSubscriptions().size();
		assertEquals(self.getSubscriptions().size(), result);

		// Assert purity:
		assertPurity(self);

		// Invariant:
		assertInvariant(self);
	}

	/**
	 * Test method for method getPosts
	 *
	 * Renvoie une nouvelle instance de ArrayList contenant la liste des posts de cet utilisateur. La
	 * liste renvoyée est triée selon leurs dates, les messages les plus récents
	 * étant en tête de liste.
	 */
	@ParameterizedTest
	@MethodSource("userProvider")
	public void testgetPosts(User self) {
		assumeTrue(self != null);

		// Invariant:
		assertInvariant(self);

		// Pré-conditions:

		// Save state for purity check:
		saveState(self);

		// Oldies:

		// Exécution:
		ArrayList<Post> result = self.getPosts();

		// Post-conditions:
		// @ensures (\forall int i, j; i >= 0 && i < j && j < \result.size(); \result.get(i).isAfter(\result.get(j)));
		for (int i = 0; i < result.size() - 1; i++) {
			assertTrue(result.get(i).isAfter(result.get(i + 1)));
		}
		// Pour tester l'indépendance:
		assertFalse(result == self.getPosts());
		result.clear();

		// Assert purity:
		assertPurity(self);

		// Invariant:
		assertInvariant(self);
	}

	/**
	 * Test method for method addPost
	 *
	 * Crée et renvoie une nouvelle instance de Post dont le texte est la chaîne de
	 * caractère spécifiée et l'ajoute à la liste des posts de cet utilisateur. La
	 * date de ce Post est la date d'exécution de cette méthode.
	 */
	 @ParameterizedTest
 	@MethodSource("userAndStringProvider")
 	public void testaddPost(User self, String msg) {
 		assumeTrue(self != null);

 		// Invariant:
 		assertInvariant(self);

 		// Pré-conditions:
 		// @requires msg != null;
 		assumeTrue(msg != null);

 		// Oldies:
 		Instant oldDate = Instant.now();
 		// Wait until oldDate is really old:
 		ArrayList<User> list = new ArrayList<User>(1);
 		while (!oldDate.isBefore(Instant.now())) {
 			list.add(self);
 		}
 		// old in:@ensures getPostNb() == \old(getPostNb()) + 1;
 		int oldPostNb = self.getPostNb();
 		// @ensures \old(lastIndex() >= 0) ==> nextIndex() == \old(nextIndex() + 1);
 		int oldNextIndex = self.nextIndex();
 		// @ensures \old(lastIndex() >= 0) ==> previousIndex() == \old(previousIndex() + 1);
 		int oldPrevIndex = self.previousIndex();
 		// @ensures \old(lastIndex() >= 0) ==> lastIndex() == \old(lastIndex() + 1);
 		int oldLastIndex = self.lastIndex();

 		// Exécution:
 		Post result = self.addPost(msg);

 		// Wait until exec date is really old:
 		Instant execDate = Instant.now();
 		while (!execDate.isBefore(Instant.now())) {
 			list.add(null);
 		}
 		list = null;
 		// Post-conditions:
 		// @ensures getPost(0).equals(\result);
 		assertEquals(self.getPost(0), result);
 		// @ensures \result.getText().equals(msg);
 		assertEquals(msg, result.getText());
 		// @ensures getPostNb() == \old(getPostNb()) + 1;
 		assertEquals(oldPostNb + 1, self.getPostNb());
 		// @ensures oldDate.isBefore(\result.getDate());
 		assertTrue(oldDate.isBefore(result.getDate()));
 		// @ensures \result.getDate().isBefore(Instant.now());
 		assertTrue(result.getDate().isBefore(Instant.now()));
 		if (oldLastIndex >= 0) {
 			// @ensures \old(lastIndex() >= 0) ==> nextIndex() == \old(nextIndex() + 1);
 			assertEquals(oldNextIndex + 1, self.nextIndex());
 			// @ensures \old(lastIndex() >= 0) ==> previousIndex() == \old(previousIndex() + 1);
 			assertEquals(oldPrevIndex + 1, self.previousIndex());
 			// @ensures \old(lastIndex() >= 0) ==> lastIndex() == \old(lastIndex() + 1);
 			assertEquals(oldLastIndex + 1, self.lastIndex());
 		} else {
 			// @ensures \old(lastIndex() == -1) ==> nextIndex() == \old(nextIndex());
 			assertEquals(oldNextIndex, self.nextIndex());
 			// @ensures \old(lastIndex() == -1) ==> previousIndex() == \old(previousIndex());
 			assertEquals(oldPrevIndex, self.previousIndex());
 			// @ensures \old(lastIndex() == -1) ==> lastIndex() == \old(lastIndex());
 			assertEquals(oldLastIndex, self.lastIndex());
 		}

 		// Invariant:
 		assertInvariant(self);
 	}
	/**
	 * Test method for method getPostNb
	 *
	 * Renvoie le nombre de Post de cet utilisateur.
	 */
	@ParameterizedTest
	@MethodSource("userProvider")
	public void testgetPostNb(User self) {
		assumeTrue(self != null);

		// Invariant:
		assertInvariant(self);

		// Pré-conditions:

		// Save state for purity check:
		saveState(self);

		// Oldies:

		// Exécution:
		int result = self.getPostNb();

		// Post-conditions:
		// @ensures \result == getPosts().size();
		assertEquals(self.getPosts().size(), result);

		// Assert purity:
		assertPurity(self);

		// Invariant:
		assertInvariant(self);
	}

	/**
	 * Test method for method getPost
	 *
	 * Renvoie le ième plus récent Post de ce User.
	 */
	@ParameterizedTest
	@MethodSource("userAndIntProvider")
	public void testgetPost(User self, int i) {
		assumeTrue(self != null);

		// Invariant:
		assertInvariant(self);

		// Pré-conditions:
		// @requires i >= 0 && i < getPostNb();
		assumeTrue(i >= 0 && i < self.getPostNb());

		// Save state for purity check:
		saveState(self);

		// Oldies:

		// Exécution:
		Post result = self.getPost(i);

		// Post-conditions:
		// @ensures \result.equals(getPosts().get(i));
		assertEquals(self.getPosts().get(i), result);

		// Assert purity:
		assertPurity(self);

		// Invariant:
		assertInvariant(self);
	}

	/**
	 * Test method for method startIteration
	 *
	 * Initialise ce User pour le démarrage d'une nouvelle itération sur les Post de
	 * ce User. Cette itération s'effectue à partir du Post le plus récent, de sorte
	 * que chaque appel à next() renvoie un Post plus ancien.
	 */
	@ParameterizedTest
	@MethodSource("userProvider")
	public void teststartIteration(User self) {
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
		// @ensures nextIndex() == 0;
		assertTrue(self.nextIndex() == 0);
		// @ensures lastIndex() == -1;
		assertTrue(self.lastIndex() == -1);

		// Invariant:
		assertInvariant(self);
	}

	/**
	 * Test method for method hasNext
	 *
	 * Renvoie true si ce User possède un Post plus ancien pour l'itération en
	 * cours.
	 */
	@ParameterizedTest
	@MethodSource("userProvider")
	public void testhasNext(User self) {
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
		// @ensures \result <==> nextIndex() < getPostNb();
		assertEquals(self.nextIndex() < self.getPostNb(), result);

		// Assert purity:
		assertPurity(self);

		// Invariant:
		assertInvariant(self);
	}

	/**
	 * Test method for method next
	 *
	 * Renvoie le Post suivant (plus ancien) dans l'itération en cours et avance
	 * d'un élément dans l'itération.
	 */
	@ParameterizedTest
	@MethodSource("userProvider")
	public void testnext(User self) {
		assumeTrue(self != null);

		// Invariant:
		assertInvariant(self);

		// Pré-conditions:
		// @requires hasNext();
		assumeTrue(self.hasNext());

		// Oldies:
		// old in:@ensures \result.equals(\old(getPost(nextIndex())));
		Post oldNextPost = self.getPost(self.nextIndex());
		// old in:@ensures nextIndex() == \old(nextIndex()) + 1;
		int oldNextIndex = self.nextIndex();
		// old in:@ensures previousIndex() == \old(previousIndex()) + 1;
		int oldPrevIndex = self.previousIndex();
		// old in:@ensures previousIndex() == \old(nextIndex());
		// old in:@ensures lastIndex() == \old(nextIndex());

		// Exécution:
		Post result = self.next();

		// Post-conditions:
		// @ensures \result.equals(\old(getPost(nextIndex())));
		assertEquals(oldNextPost, result);
		// @ensures \result.equals(getPost(lastIndex()));
		assertEquals(self.getPost(self.lastIndex()), result);
		// @ensures nextIndex() == \old(nextIndex()) + 1;
		assertEquals(oldNextIndex + 1, self.nextIndex());
		// @ensures previousIndex() == \old(previousIndex()) + 1;
		assertEquals(oldPrevIndex + 1, self.previousIndex());
		// @ensures previousIndex() == \old(nextIndex());
		assertEquals(oldNextIndex, self.previousIndex());
		// @ensures lastIndex() == \old(nextIndex());
		assertEquals(oldNextIndex, self.lastIndex());
		// @ensures lastIndex() == previousIndex();
		assertEquals(self.previousIndex(), self.lastIndex());

		// Invariant:
		assertInvariant(self);
	}

	/**
	 * Test method for method nextIndex
	 *
	 * Renvoie l'index du Post qui sera renvoyé par le prochain appel à next(). Si
	 * l'itération est arrivée à la fin la valeur renvoyée est getPostNb().
	 */
	@ParameterizedTest
	@MethodSource("userProvider")
	public void testnextIndex(User self) {
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
		// @ensures \result == getPostNb() <==> !hasNext();
		assertEquals(!self.hasNext(), result == self.getPostNb());
		// @ensures hasNext() <==> \result >= 0 && \result < getPostNb();
		assertEquals(self.hasNext(), result >= 0 && result < self.getPostNb());

		// Assert purity:
		assertPurity(self);

		// Invariant:
		assertInvariant(self);
	}

	/**
	 * Test method for method hasPrevious
	 *
	 * Renvoie true si ce User possède un Post plus récent pour l'itération en
	 * cours.
	 */
	@ParameterizedTest
	@MethodSource("userProvider")
	public void testhasPrevious(User self) {
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
		// @ensures \result <==> previousIndex() >= 0;
		assertEquals(self.previousIndex() >= 0, result);
		// @ensures !\result <==> previousIndex() == -1;
		assertEquals(self.previousIndex() == -1, !result);

		// Assert purity:
		assertPurity(self);

		// Invariant:
		assertInvariant(self);
	}

	/**
	 * Test method for method previous
	 *
	 * Renvoie le Post précedent (plus récent) dans l'itération en cours et recule
	 * d'un élément dans l'itération.
	 */
	@ParameterizedTest
	@MethodSource("userProvider")
	public void testprevious(User self) {
		assumeTrue(self != null);

		// Invariant:
		assertInvariant(self);

		// Pré-conditions:
		// @requires hasPrevious();
		assumeTrue(self.hasPrevious());

		// Oldies:
		// old in:@ensures \result.equals(\old(getPost(previousIndex())));
		Post oldPrevPost = self.getPost(self.previousIndex());
		// old in:@ensures nextIndex() == \old(nextIndex()) - 1;
		int oldNextIndex = self.nextIndex();
		// old in:@ensures previousIndex() == \old(previousIndex()) - 1;
		// old in:@ensures nextIndex() == \old(previousIndex());
		// old in:@ensures lastIndex() == \old(previousIndex());
		int oldPrevIndex = self.previousIndex();

		// Exécution:
		Post result = self.previous();

		// Post-conditions:
		// @ensures \result.equals(\old(getPost(previousIndex())));
		assertEquals(oldPrevPost, result);
		// @ensures \result.equals(getPost(lastIndex()));
		assertEquals(self.getPost(self.lastIndex()), result);
		// @ensures nextIndex() == \old(nextIndex()) - 1;
		assertEquals(oldNextIndex - 1, self.nextIndex());
		// @ensures previousIndex() == \old(previousIndex()) - 1;
		assertEquals(oldPrevIndex - 1, self.previousIndex());
		// @ensures nextIndex() == \old(previousIndex());
		assertEquals(oldPrevIndex, self.nextIndex());
		// @ensures lastIndex() == \old(previousIndex());
		assertEquals(oldPrevIndex, self.lastIndex());
		// @ensures lastIndex() == nextIndex();
		assertTrue(self.lastIndex() == self.nextIndex());

		// Invariant:
		assertInvariant(self);
	}

	/**
	 * Test method for method previousIndex
	 *
	 * Renvoie l'index du Post qui sera renvoyé par le prochain appel à previous().
	 * Si l'itération est arrivée au début la valeur renvoyée est -1.
	 */
	@ParameterizedTest
	@MethodSource("userProvider")
	public void testpreviousIndex(User self) {
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
		// @ensures \result == -1 <==> !hasPrevious();
		assertEquals(!self.hasPrevious(), result == -1);
		// @ensures hasPrevious() <==> \result >= 0 && \result < getPostNb();
		assertEquals(self.hasPrevious(), result >= 0 && result < self.getPostNb());

		// Assert purity:
		assertPurity(self);

		// Invariant:
		assertInvariant(self);
	}

	/**
	 * Test method for method lastIndex
	 *
	 * Renvoie l'index du Post renvoyé par le dernier appel à previous() ou next().
	 * Si previous() et next() n'ont pas été appelée depuis le dernier appel à
	 * StartIteration() (ou l'appel du constructeur), renvoie -1.
	 */
	@ParameterizedTest
	@MethodSource("userProvider")
	public void testlastIndex(User self) {
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
		// @ensures \result == nextIndex() || \result == previousIndex();
		assertTrue(result == self.nextIndex() || result == self.previousIndex());

		// Assert purity:
		assertPurity(self);

		// Invariant:
		assertInvariant(self);
	}
} // End of the test class for User
