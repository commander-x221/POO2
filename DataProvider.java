/**
 * 
 */
package sociald1.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import sociald1.NewsFeed;
import sociald1.Post;
import sociald1.User;

/**
 * 
 */
public class DataProvider {
	static final int LG_STREAM = 500;
	private static List<String> goodUserNames = Arrays.asList("Marcel", "Adam", "Sonia",
            "Idir", "Mohamed", "Marc", "Ali", "Ziad",
            "Lyes", "Ayman", "Mounir", "Pierre", "Chanez",
            "Lamia", "Yanis", "Faycal", "Boris", "Imam",
            "Naila", "Zahra", "Rosa", "Lisa", "Sheraz",
            "Nima", "Aliou", "Issa", "Mamadou", "Ismael");
	private static List<String> badNames = Arrays.asList("", " ", "  ", "\n \t", null);
	private static List<User> allUsers;
	private static List<Post> allPosts;
	private static Random randGen = new Random();

	static {
		initUsers(goodUserNames);
	}

	private static void initUsers(List<String> userNames) {
		allUsers = new ArrayList<User>(userNames.size());
		for (String name : userNames) {
			allUsers.add(new User(name, "pass" + name));
		}
		addPostsToUsers(allUsers);
		addLikesToPosts(allPosts);
		addSubscriptionsToUsers(allUsers);
	}

	private static void addPostsToUsers(List<User> userList) {
		int nbMsg = 1000;
		allPosts = new ArrayList<Post>(nbMsg);
		while (nbMsg > 0) {
			User u = getRandomElt(userList);
			allPosts.add(u.addPost("Message n°" + nbMsg + " from " + u.getName()));
			nbMsg--;
		}
	}

	private static void addSubscriptionsToUsers(List<User> userList) {
		int nbSubscription = 100;
		while (nbSubscription > 0) {
			User u = getRandomElt(userList);
			User subscription = getRandomElt(userList);
			if (u != subscription) {
				u.addSubscription(subscription);
				nbSubscription--;
			}
		}
	}

	private static void addLikesToPosts(List<Post> postList) {
		int likeNb = 1000;
		while (likeNb > 0) {
			getRandomElt(postList).addLikeFrom(getRandomElt(allUsers));
			likeNb--;
		}
	}

	public static List<User> allUser() {
		return Collections.unmodifiableList(allUsers);
	}
	
	public static Post postSupplier() {
		return getRandomElt(allPosts);
	}

	public static User userSupplier () {
		return getRandomElt(allUsers);
	}
	
	public static NewsFeed newsFeedSupplier() {
		return new NewsFeed(getRandomElt(allUsers));
	}

	public static String stringSupplier() {
		if (randBool(5)) {
			return getRandomElt(badNames);
		}
		return getRandomElt(goodUserNames);
	}
	/**
	 * Renvoie un élément tiré aléatoirement parmi les éléments de la collection
	 * spécifiée.
	 *
	 * @requires c != null;
	 * @requires !c.isEmpty();
	 * @ensures c.contains(\result);
	 *
	 * @param <T> Type des éléments de la collection spécifiée
	 * @param c   collection dans laquelle est choisi l'élément retourné
	 *
	 * @return un élément tiré aléatoirement parmi les éléments de la collection
	 *         spécifiée
	 * 
	 * @throws NullPointerException     si l'argument spécifié est null
	 * @throws IllegalArgumentException si l'argument spécifié est vide
	 */
	public static <T> T getRandomElt(Collection<T> c) {
		int index = randInt(c.size());
		if (c instanceof List<?>) {
			return ((List<T>) c).get(index);
		}
		int i = 0;
		for (T elt : c) {
			if (i == index) {
				return elt;
			}
			i++;
		}
		throw new NoSuchElementException(); // Ne peut pas arriver
	}
	
	/**
	 * Renvoie un int obtenue par un générateur pseudo-aléatoire.
	 *
	 * @param max la valeur maximale du nombre aléatoire attendu
	 *
	 * @return un entier >= 0 et < max
	 *
	 * @throws IllegalArgumentException si max <= 0
	 *
	 * @requires max > 0;
	 * @ensures \result >= 0;
	 * @ensures \result < max;
	 */
	public static int randInt(int max) {
		return randGen.nextInt(max);
	}
	
	/**
	 * Renvoie une valeur booléenne obtenue par un générateur pseudo-aléatoire. La
	 * valeur renvoyée a une probabilité d'être true similaire à la probabilité que
	 * randInt(max) renvoie la valeur 0.
	 *
	 * @return une valeur booléenne aléatoire
	 * 
	 * @throws IllegalArgumentException si max <= 0
	 * 
	 * @requires max > 0;
	 */
	public static boolean randBool(int max) {
		return randGen.nextInt(max) == 0;
	}}