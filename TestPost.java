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
 * Test class for Post.
 *
 * Un post d'un utilisateur du réseau social Microdon, caractérisé par son
 * texte, sa date de création et l'ensemble des utilisateurs l'ayant "liké".
 * 
 * La seule caractéristque modifiable de cette classe est l'ensemble des
 * utilisteurs ayant "liké" ce Post.
 */
public class TestPost {

	public static Stream<Post> postProvider() {
		return Stream.generate(() -> postSupplier()).limit(LG_STREAM);
	}
	
	public static Stream<String> stringProvider() {
		return Stream.generate(() -> stringSupplier()).limit(LG_STREAM);
	}

	public static Stream<Arguments> postAndUserProvider() {
		return Stream.generate(() -> Arguments.of(postSupplier(), userSupplier())).limit(LG_STREAM);
	}
	
	public static Stream<Arguments> postAndPostProvider() {
		return Stream.generate(() -> Arguments.of(postSupplier(), postSupplier())).limit(LG_STREAM);
	}

	private Instant dateCreation;
	private String text;
	private Set<User> likers;

	private void saveState(Post self) {
		// Put here the code to save the state of self:
		this.dateCreation = self.getDate();
		this.text = self.getText();
		this.likers = new HashSet<User>(self.getLikers());
	}

	private void assertPurity(Post self) {
		// Put here the code to check purity for self:
		assertEquals(dateCreation, self.getDate());
		assertEquals(text, self.getText());
		assertEquals(likers, self.getLikers());
	}

	public void assertInvariant(Post self) {
		// Put here the code to check the invariant:
		// @invariant getText() != null;
		assertNotNull(self.getText());
		// @invariant getDate() != null;
		assertNotNull(self.getDate());
		// @invariant getLikers() != null && !getLikers().contains(null);
		assertNotNull(self.getLikers());
		assertFalse(self.getLikers().contains(null));
	}

	/**
	 * Test method for constructor Post
	 *
	 * Initialise un nouveau Post. La date de ce nouveau Post est la date courante
	 * au moment de l'exécution de ce constructeur.
	 */
	@ParameterizedTest
	@MethodSource("stringProvider")
	public void testPost(String text) {

		// Pré-conditions:
		// @requires text != null;
		assumeTrue(text != null);

		// Oldies:
		Instant oldDate = Instant.now();
		// Wait until oldDate is really old:
		ArrayList<User> list = new ArrayList<User>(1);
		while (!oldDate.isBefore(Instant.now())) {
			list.add(null);
		}
		
		// Exécution:
		Post result = new Post(text);

		// Wait until exec date is really old:
		Instant execDate = Instant.now();
		while (!execDate.isBefore(Instant.now())) {
			list.add(null);
		}
		list = null;
		// Post-conditions:
		// @ensures getText().equals(text);
		assertEquals(text, result.getText());
		// @ensures oldDate.isBefore(getDate());
		assertTrue(oldDate.isBefore(result.getDate()));
		// @ensures getDate().isBefore(Instant.now());
		assertTrue(result.getDate().isBefore(Instant.now()));
		// @ensures getLikers().isEmpty();
		assertTrue(result.getLikers().isEmpty());

		// Invariant:
		assertInvariant(result);
	}

	/**
	 * Test method for method getDate
	 *
	 * Renvoie une nouvelle instance de Date représentant la date de création de ce
	 * Post.
	 */
	@ParameterizedTest
	@MethodSource("postProvider")
	public void testgetDate(Post self) {
		assumeTrue(self != null);

		// Invariant:
		assertInvariant(self);

		// Pré-conditions:

		// Save state for purity check:
		saveState(self);

		// Oldies:

		// Exécution:
		Instant result = self.getDate();

		// Post-conditions:

		// Assert purity:
		assertPurity(self);

		// Invariant:
		assertInvariant(self);
	}

	/**
	 * Test method for method getText
	 *
	 * Renvoie le texte de ce Post.
	 */
	@ParameterizedTest
	@MethodSource("postProvider")
	public void testgetText(Post self) {
		assumeTrue(self != null);

		// Invariant:
		assertInvariant(self);

		// Pré-conditions:

		// Save state for purity check:
		saveState(self);

		// Oldies:

		// Exécution:
		String result = self.getText();

		// Post-conditions:

		// Assert purity:
		assertPurity(self);

		// Invariant:
		assertInvariant(self);
	}

	/**
	 * Test method for method getLikeNumber
	 *
	 * Renvoie le nombre de like, c'est à dire le nombre d'utilisateurs ayant "liké"
	 * ce Post.
	 */
	@ParameterizedTest
	@MethodSource("postProvider")
	public void testgetLikeNumber(Post self) {
		assumeTrue(self != null);

		// Invariant:
		assertInvariant(self);

		// Pré-conditions:

		// Save state for purity check:
		saveState(self);

		// Oldies:

		// Exécution:
		int result = self.getLikeNumber();

		// Post-conditions:
		// @ensures \result == getLikers().size();
		assertEquals(self.getLikers().size(), result);

		// Assert purity:
		assertPurity(self);

		// Invariant:
		assertInvariant(self);
	}

	/**
	 * Test method for method hasLikeFrom
	 *
	 * Renvoie true si l'utilisateur spécifié fait partie des "likers" de ce Post.
	 */
	@ParameterizedTest
	@MethodSource("postAndUserProvider")
	public void testhasLikeFrom(Post self, User u) {
		assumeTrue(self != null);

		// Invariant:
		assertInvariant(self);

		// Pré-conditions:

		// Save state for purity check:
		saveState(self);

		// Oldies:

		// Exécution:
		boolean result = self.hasLikeFrom(u);

		// Post-conditions:
		// @ensures \result <==> getLikers().contains(u);
		assertEquals(self.getLikers().contains(u), result);

		// Assert purity:
		assertPurity(self);

		// Invariant:
		assertInvariant(self);
	}

	/**
	 * Test method for method addLikeFrom
	 *
	 * Ajoute un utilisateur à l'ensemble des utilisateurs ayant "liké" ce message.
	 * L'auteur d'un Post a la possibilité de "liker" les messages dont il est
	 * l'auteur.
	 */
	@ParameterizedTest
	@MethodSource("postAndUserProvider")
	public void testaddLikeFrom(Post self, User u) {
		assumeTrue(self != null);

		// Invariant:
		assertInvariant(self);

		// Pré-conditions:
		// @requires u != null;
		assumeTrue(u != null);

		// Oldies:
		// old in:@ensures \result <==> !\old(hasLikeFrom(u));
		boolean oldHasLike = self.hasLikeFrom(u);
		// old in:@ensures \result ==> (getLikeNumbe() == \old(getLikeNumber() + 1));
		// old in:@ensures !\result ==> (getLikeNumber() == \old(getLikeNumber()));
		int oldLikeNb = self.getLikeNumber();

		// Exécution:
		boolean result = self.addLikeFrom(u);

		// Post-conditions:
		// @ensures hasLikeFrom(u);
		assertTrue(self.hasLikeFrom(u));
		// @ensures \result <==> !\old(hasLikeFrom(u));
		assertEquals(!oldHasLike, result);
		if (result) {
			// @ensures \result ==> (getLikeNumbre() == \old(getLikeNumber() + 1));
			assertEquals(oldLikeNb + 1, self.getLikeNumber());
		} else {
			// @ensures !\result ==> (getLikeNumbre() == \old(getLikeNumber()));
			assertEquals(oldLikeNb, self.getLikeNumber());
		}

		// Invariant:
		assertInvariant(self);
	}

	/**
	 * Test method for method getLikers
	 *
	 * Renvoie une nouvelle instance de HashSet contenant l'ensemble des "likers" de
	 * ce Post.
	 */
	@ParameterizedTest
	@MethodSource("postProvider")
	public void testgetLikers(Post self) {
		assumeTrue(self != null);

		// Invariant:
		assertInvariant(self);

		// Pré-conditions:

		// Save state for purity check:
		saveState(self);

		// Oldies:

		// Exécution:
		HashSet<User> result = self.getLikers();

		// Post-conditions:
		// @ensures \result != null;
		assertNotNull(result);
		// @ensures (\forall User u; hasLikeFrom(u); \result.contains(u));
		for (User u : self.getLikers()) {
			assertTrue(self.hasLikeFrom(u));
			assertTrue(result.contains(u));
		}
		// Test de l'indépenance:
		assertTrue(result != self.getLikers());
		result.clear();

		// Assert purity:
		assertPurity(self);

		// Invariant:
		assertInvariant(self);
	}

	/**
	 * Test method for method isBefore
	 *
	 * Teste si ce Post a été publié avant le Post spécifié.
	 */
	@ParameterizedTest
	@MethodSource("postAndPostProvider")
	public void testisBefore(Post self, Post p) {
		assumeTrue(self != null);

		// Invariant:
		assertInvariant(self);

		// Pré-conditions:
		// @requires p != null;
		assumeTrue(p != null);

		// Save state for purity check:
		saveState(self);

		// Oldies:

		// Exécution:
		boolean result = self.isBefore(p);

		// Post-conditions:
		// @ensures \result <==> this.getDate().isBefore(p.getDate());
		assertEquals(self.getDate().isBefore(p.getDate()), result);
		// @ensures !(\result && this.isAfter(p));
		assertFalse(result && self.isAfter(p));

		// Assert purity:
		assertPurity(self);

		// Invariant:
		assertInvariant(self);
	}

	/**
	 * Test method for method isAfter
	 *
	 * Teste si ce Post a été publié après le Post spécifié.
	 */
	@ParameterizedTest
	@MethodSource("postAndPostProvider")
	public void testisAfter(Post self, Post p) {
		assumeTrue(self != null);

		// Invariant:
		assertInvariant(self);

		// Pré-conditions:
		// @requires p != null;
		assumeTrue(p != null);

		// Save state for purity check:
		saveState(self);

		// Oldies:

		// Exécution:
		boolean result = self.isAfter(p);

		// Post-conditions:
		// @ensures \result <==> this.getDate().isAfter(p.getDate());
		assertEquals(self.getDate().isAfter(p.getDate()), result);
		// @ensures !(\result && this.isBefore(p));
		assertFalse(result && self.isBefore(p));

		// Assert purity:
		assertPurity(self);

		// Invariant:
		assertInvariant(self);
	}
} // End of the test class for Post
