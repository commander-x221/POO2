package sociald1;

import java.time.Instant;
import java.util.HashSet;

/**
 * @author Mamadou NDIAYE 12109680
 * Je déclare qu'il s'agit de mon propre travail
 * Un post d'un utilisateur du réseau social Microdon, caractérisé par son
 * texte, sa date de création et l'ensemble des utilisateurs l'ayant "liké".
 * 
 * La seule caractéristque modifiable de cette classe est l'ensemble des
 * utilisteurs ayant "liké" ce Post.
 * 
 * @invariant getText() != null;
 * @invariant getDate() != null;
 * @invariant getLikers() != null && !getLikers().contains(null);
 * 
 *
 * @since 17/09/2023
 * @version 3/10/2023
 */
public class Post {
		// À compléter
	private String text;
    private Instant creationDate;
    private HashSet<User> likers;


	/**
	 * Initialise un nouveau Post. La date de ce nouveau Post est la date courante
	 * au moment de l'exécution de ce constructeur.
	 * 
	 * @param text le texte du Post
	 * 
	 * @requires text != null;
	 * @ensures getText().equals(text);
	 * @ensures \old(Instant.now()).isBefore(getDate());
	 * @ensures getDate().isBefore(Instant.now());
	 * @ensures getLikers().isEmpty();
	 */
	public Post(String text) {
		// À compléter
		this.text = text;
        this.creationDate = Instant.now();
        this.likers = new HashSet<>();
	}

	/**
	 * Renvoie la date de création de ce Post.
	 * 
	 * @return la date de création de ce Post
	 * 
	 * @pure
	 */
	public Instant getDate() {
		// À compléter
		return this.creationDate;
	}

	/**
	 * Renvoie le texte de ce Post.
	 * 
	 * @return le texte de ce Post
	 * 
	 * @pure
	 */
	public String getText() {
		// À compléter
		return this.text;
	}

	/**
	 * Renvoie le nombre de like, c'est à dire le nombre d'utilisateurs distincts
	 * ayant "liké" ce Post.
	 * 
	 * @return le nombre de like de ce Post
	 * 
	 * @ensures \result == getLikers().size();
	 * 
	 * @pure
	 */
	public int getLikeNumber() {
		// À compléter
		return this.likers.size();
	}

	/**
	 * Renvoie true si l'utilisateur spécifié fait partie des "likers" de ce Post.
	 * 
	 * @param u utilisateur donc on souhaite savoir s'il a "liké" de ce message
	 * @return true si l'utilisateur spécifié a "liké" de ce Post; false sinon
	 * 
	 * @ensures \result <==> getLikers().contains(u);
	 * 
	 * @pure
	 */
	public boolean hasLikeFrom(User u) {
		// À compléter
		return likers.contains(u);
	}

	/**
	 * Ajoute un utilisateur à l'ensemble des utilisateurs ayant "liké" ce message.
	 * L'auteur d'un Post a la possibilité de "liker" les messages dont il est
	 * l'auteur.
	 * 
	 * @param u utilisateur ayant "liké" ce message
	 * @return true si l'utilisateur ne faisait pas déjà partie des "likers"; false
	 *         sinon
	 * 
	 * @requires u != null;
	 * @ensures hasLikeFrom(u);
	 * @ensures \result <==> !\old(hasLikeFrom(u));
	 * @ensures \result ==> (getLikeNumber() == \old(getLikeNumber() + 1));
	 * @ensures !\result ==> (getLikeNumber() == \old(getLikeNumber()));
	 * 
	 */
	public boolean addLikeFrom(User u) {
		// À compléter
		return likers.add(u);
	}

	/**
	 * Renvoie une nouvelle instance de HashSet contenant l'ensemble des "likers" de
	 * ce Post.
	 * 
	 * @return l'ensemble des "likers" de ce Post
	 * 
	 * @ensures \result != null;
	 * @ensures (\forall User u; hasLikeFrom(u); \result.contains(u));
	 * 
	 * @pure
	 */
	public HashSet<User> getLikers() {
		// À compléter
		return new HashSet<>(this.likers);
	}

	/**
	 * Teste si ce Post a été publié avant le Post spécifié.
	 * 
	 * @param p le Post à comparer avec ce Post
	 * 
	 * @return true si et seulement si la date de ce Post est strictement antérieure
	 *         à celle du Post spécifié
	 * 
	 * @requires p != null;
	 * @ensures \result <==> this.getDate().isBefore(p.getDate());
	 * @ensures !(\result && this.isAfter(p));
	 * 
	 * @pure
	 */
	public boolean isBefore(Post p) {
		// À compléter
		return this.creationDate.isBefore(p.getDate());
	}

	/**
	 * Teste si ce Post a été publié après le Post spécifié.
	 * 
	 * @param p le Post à comparer avec ce Post
	 * 
	 * @return true si et seulement si la date de ce Post est strictement postérieure
	 *         à celle du Post spécifié
	 * 
	 * @requires p != null;
	 * @ensures \result <==> this.getDate().isAfter(p.getDate());
	 * @ensures !(\result && this.isBefore(p));
	 * 
	 * @pure
	 */
	public boolean isAfter(Post p) {
		// À compléter
		return this.creationDate.isAfter(p.getDate());
	}
}
