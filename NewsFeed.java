/**
 * 
 */
package sociald1;

/**
 * @author Mamadou NDIAYE 12109680
 * Je déclare qu'il s'agit de mon propre travail
 * 
 * Fil d'actualité d'un utilisateur du réseau social Microdon.
 * 
 * Un NewsFeed regroupe les Post d'un User et ceux de tous les User auxquels il
 * est abonné. Il permet de naviguer dans la liste de tous ces Post qui est
 * ordonnée du plus récent au plus ancien. Cette navigation doit être initiée
 * par un appel à la méthode startIteration() ou au constructeur, ensuite,
 * l'appel à la méthode next() permet d'aller vers les messages plus anciens et
 * l'appel à la méthode previous() permet d'aller vers les Post plus
 * récents.
 * 
 * Chaque itération utilise les méthodes d'itération des User concernés par ce
 * NewsFeed. Lorsqu'une itération a été initié par un appel à la méthode
 * startIteration() ou au constructeur, aucun des utilisateurs ne doit poster de
 * nouveaux messages. Si cette condition n'est pas respectée les résultats
 * obtenus sont imprévisibles. Cependant, un nouvel appel à startIteration()
 * permet de rétablir un fonctionnement normal pour une nouvelle itération.
 * 
 * @invariant getUser() != null;
 * @invariant size() == getUser().getPostNb() + (\sum User u;
 *            getUser().getSubscriptions().contains(u); u.getPostNb());
 * @invariant nextIndex() == previousIndex() + 1;
 * @invariant lastIndex() == nextIndex() || lastIndex() == previousIndex();
 * @invariant previousIndex() >= -1 && previousIndex() < size();
 * @invariant nextIndex() >= 0 && nextIndex() <= size();
 * @invariant lastIndex() >= -1 && lastIndex() < size();
 * @invariant lastUser() == null || lastUser().equals(getUser()) ||
 *            getUser().getSubscriptions().contains(lastUser());
 * @invariant lastIndex() == -1 <==> lastUser() == null;
 * @invariant !hasPrevious() <==> previousIndex() == -1;
 * @invariant !hasNext() <==> nextIndex() == size();
 * 
 * 
 * @since 17/09/2023
 * @version 18/09/2023
 */
public class NewsFeed {
	// À compléter
	private User user;
    private int previousIdx;
    private int nextIdx;
    private int lastIdx;
    private User lastUser;

	/**
	 * Initialise un nouveau NewsFeed pour l'utilisateur spécifié. Le NewsFeed est
	 * initialisé de manière à pouvoir commencer une itération de la même manière
	 * que si la méthode startIteration avait été appelée.
	 * 
	 * @param aUser l'utilisateur dont on souhaite explorer le NewsFeed
	 * 
	 * @requires aUser != null;
	 * @ensures getUser().equals(aUser);
	 * @ensures !hasPrevious();
	 * @ensures previousIndex() == -1;
	 * @ensures hasNext() <==> size() > 0;
	 * @ensures nextIndex() == 0;
	 * @ensures lastIndex() == -1;
	 * @ensures lastUser() == null;
	 */
	public NewsFeed(User aUser) {
		// À compléter
		this.user = aUser;
        startIteration();
	}

	/**
	 * Renvoie l'utilisateur dont ce NewsFeed est le fil d'actualité.
	 * 
	 * @return l'utilisateur dont ce NewsFeed est le fil d'actualité
	 * 
	 * @pure
	 */
	public User getUser() {
		// À compléter
		return this.user;
	}

	/**
	 * Démarre une nouvelle itération de ce NewsFeed.
	 * 
	 * @ensures !hasPrevious();
	 * @ensures previousIndex() == -1;
	 * @ensures hasNext() <==> size() > 0;
	 * @ensures nextIndex() == 0;
	 * @ensures lastIndex() == -1;
	 * @ensures lastUser() == null;
	 * @ensures !getUser().hasPrevious();
	 * @ensures (\forall User u; getUser().equals(u) ||
	 *          getUser().getSubscriptions().contains(u); !u.hasPrevious());
	 * @ensures (\forall User u; getUser().equals(u) ||
	 *          getUser().getSubscriptions().contains(u); u.lastIndex() == -1);
	 */
	public void startIteration() {
		// À compléter
		this.previousIdx = -1;
        this.nextIdx = 0;
        this.lastIdx = -1;
        this.lastUser = null;
        user.startIteration();
	}

	/**
	 * Renvoie true si et seulement si cette itération possède un Post précédent
	 * (i.e. plus récent).
	 * 
	 * @return true si cette itération possède un Post précédent; false sinon
	 * 
	 * @ensures \result <==> (\exists User u; getUser().equals(u) ||
	 *          getUser().getSubscriptions().contains(u); u.hasPrevious());
	 * @pure
	 */
	public boolean hasPrevious() {
		// À compléter
		return user.hasPrevious();
	}

	/**
	 * Renvoie, s'il en existe un, l'index du Post précédent dans cette itération.
	 * Renvoie -1 si cette itération ne possède pas de Post précédent.
	 * 
	 * @return l'index du Post précédent dans cette itération s'il un tel élement
	 *         existe; -1 sinon
	 * 
	 * @pure
	 */
	public int previousIndex() {
		// À compléter
		return this.previousIdx;
	}

	/**
	 * Renvoie le Post précédent (plus récent) de cette itération et recule d'un
	 * élement. Un appel postérieur à next() renverra le même élement.
	 * 
	 * @return le Post précédent de cette itération
	 * 
	 * @requires hasPrevious();
	 * @ensures \result != null;
	 * @ensures previousIndex() == \old(previousIndex()) - 1;
	 * @ensures nextIndex() == \old(nextIndex()) - 1;
	 * @ensures lastIndex() == \old(previousIndex());
	 * @ensures lastUser() != null;
	 * @ensures \result.equals(lastUser().getPost(lastUser().lastIndex()));
	 */
	public Post previous() {
		// À compléter
		Post post = user.previous();
        if (post != null) {
            this.previousIdx--;
            this.nextIdx--;
            this.lastIdx = this.previousIdx;
            this.lastUser = user;
        }
        return post;
	}

	/**
	 * Renvoie true si et seulement si cette itération possède un Post postérieur
	 * (i.e. plus ancien).
	 * 
	 * @return true si cette itération possède un Post postérieur; false sinon
	 * 
	 * @ensures \result <==> (\exists User u; getUser().equals(u) ||
	 *          getUser().getSubscriptions().contains(u); u.hasNext());
	 * @pure
	 */
	public boolean hasNext() {
		// À compléter
		return user.hasNext();
	}

	/**
	 * Renvoie le Post suivant (plus ancien) de cette itération et avance d'un
	 * élement dans l'itération. Un appel ultérieur à prévious() renverra le même
	 * élement.
	 * 
	 * @return le Post suivant de cette itération
	 * 
	 * @requires hasNext();
	 * @ensures \result != null;
	 * @ensures previousIndex() == \old(previousIndex()) + 1;
	 * @ensures nextIndex() == \old(nextIndex()) + 1;
	 * @ensures lastIndex() == \old(nextIndex());
	 * @ensures lastUser() != null;
	 * @ensures \result.equals(lastUser().getPost(lastUser().lastIndex()));
	 */
	public Post next() {
		// À compléter
		Post post = user.next();
        if (post != null) {
            this.previousIdx++;
            this.nextIdx++;
            this.lastIdx = this.nextIdx;
            this.lastUser = user;
        }
        return post;
	}

	/**
	 * Renvoie, s'il en existe un, l'index du Post suivant dans cette itération.
	 * Renvoie -1 si cette itération ne possède pas de Post suivant.
	 * 
	 * @return l'index du Post suivant dans cette itération s'il un tel élement
	 *         existe; -1 sinon
	 * 
	 * @pure
	 */
	public int nextIndex() {
		// À compléter
		return this.nextIdx;
	}

	/**
	 * Renvoie l'index dans ce NewsFeed de l'élement renvoyé par le dernier appel à
	 * next() ou previous(). Renvoie -1 si aucun appel à next() ou prévious() n'a
	 * été effectué depuis le démarrage de cette itération (i.e. depuis la création
	 * de l'instance ou le dernier appel à startIteration()).
	 * 
	 * @return l'index de l'élement renvoyé par le dernier appel à next() ou
	 *         previous()
	 * 
	 * @pure
	 */
	public int lastIndex() {
		// À compléter
		return this.lastIdx;
	}

	/**
	 * Renvoie l'auteur du Post renvoyé par le dernier appel à next() ou previous().
	 * Renvoie null si aucun appel à next() ou prévious() n'a été effectué depuis le
	 * démarrage de cette itération (i.e. depuis la création de l'instance ou le
	 * dernier appel à startIteration()).
	 * 
	 * @return l'auteur du Post renvoyé par le dernier appel à next() ou previous()
	 * 
	 * @ensures \result != null ==> getUser().getSubscriptions().contains(\result);
	 * 
	 * @pure
	 */
	public User lastUser() {
		// À compléter
		return this.lastUser;
	}

	/**
	 * Renvoie le nombre total de Post de ce NewsFeed.
	 * 
	 * @return le nombre total de Post de ce NewsFeed
	 * 
	 * @pure
	 */
	public int size() {
		// À compléter
		return user.getPostNb() + user.getSubscriptions().stream().mapToInt(User::getPostNb).sum();
	}
}
