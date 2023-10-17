// 3.1
fun <A> tail(xs: List<A>): List<A> =
    when (xs) {
        is Nil -> throw IllegalStateException() // Nil
        is Cons -> xs.tail
    }

// 3.2
fun <A> setHead(xs: List<A>, x: A): List<A> =
    when (xs) {
        is Nil -> throw IllegalStateException() // Nil
        is Cons -> Cons(x, xs.tail)
    }

// 3.3
fun <A> drop(l: List<A>, n: Int): List<A> {
    if (n == 0) return l

    return when (l) {
        is Nil -> throw IllegalStateException() // Nil
        is Cons -> drop(l.tail, n - 1)
    }
}

// 3.4
fun <A> dropWhile(l: List<A>, f: (A) -> Boolean): List<A> {
    return when (l) {
        is Nil -> Nil
        is Cons -> if (f(l.head)) dropWhile(l.tail, f) else l
    }
}

// 3.5
fun <A> init(l: List<A>): List<A> {
    return when (l) {
        is Nil -> throw IllegalStateException()
        is Cons -> if (l.tail == Nil) return Nil else Cons(l.head, init(l.tail))
    }
}

// 3.6
// 즉시 결과를 돌려줄 수 없다. 리스트의 맨 끝까지 모든 원소를 순회하고, 그 후 익명 함수를 적용하면서 한 값으로 축약된다.
// 쇼트 서킷을 제공할 수 있으면 불필요하게 스택에 프레임을 쌓을 필요 없을 가능성이 추가되는 장점이 있을 것 같다.

// 3.7
val z = Nil as List<Int>
val f = { x: Int, y: List<Int> -> Cons(x, y) }

val trace = {
    foldRight(List.of(1, 2, 3), z, f)
    Cons(1, foldRight(List.of(2, 3), z, f))
    Cons(1, Cons(2, foldRight(List.of(3), z, f)))
    Cons(1, Cons(2, Cons(3, foldRight(List.empty(), z, f))))
    Cons(1, Cons(2, Cons(3, Nil)))
}
// 결과: Cons(head=1, tail=Cons(head=2, tail=Cons(head=3, tail=Nil)))
// Cons 생성자를 대치하게 된다.

// 3.8
fun <A> length(xs: List<A>): Int =
    foldRight(xs, 0, { _, y -> 1 + y })

// 3.9
tailrec fun <A, B> foldLeft(xs: List<A>, z: B, f: (B, A) -> B): B =
    when (xs) {
        is Nil -> z
        is Cons -> foldLeft(xs.tail, f(z, xs.head), f)
    }

// 3.10
fun sumL(xs: List<Int>): Int =
    foldLeft(xs, 0, { b, a -> b + a })

fun productL(xs: List<Double>): Double =
    foldLeft(xs, 1.0, { b, a -> b * a })

fun <A> lengthL(xs: List<A>): Int =
    foldLeft(xs, 0, { b, _ -> 1 + b })

// 3.11
fun <A> reverse(xs: List<A>): List<A> =
    foldLeft(xs, List.empty(), { y: List<A>, x: A -> Cons(x, y) })

// 3.12
fun <A, B> foldLeftR(xs: List<A>, z: B, f: (B, A) -> B): B =
    foldRight(xs, z, { a, b -> f(b, a) })

fun <A, B> foldRightL(xs: List<A>, z: B, f: (A, B) -> B): B =
    foldLeft(xs, z, { b, a -> f(a, b) })

// 3.13
fun <A> append(a1: List<A>, a2: List<A>): List<A> {
    val rightF = { x: A, y: List<A> -> Cons(x, y) }
    return foldRight(a1, a2, rightF)
}

fun <A> appendL(a1: List<A>, a2: List<A>): List<A> {
    val leftF = { y: List<A>, x: A -> Cons(x, y) }
    return foldLeft(reverse(a1), a2, leftF)
}

// 3.14
fun <A> concat(lla: List<List<A>>): List<A> =
    foldRight(lla, List.empty(), { x, y -> append(x, y) })

fun <A> concat2(lla: List<List<A>>): List<A> =
    foldLeft(lla, List.empty(), { y, x -> appendL(y, x) })

// 3.15
fun increment(xs: List<Int>): List<Int> =
    foldLeft(reverse(xs), List.empty(), { y, x -> Cons(x + 1, y) })

fun increment2(xs: List<Int>): List<Int> =
    foldRight(xs, List.empty(), { x, y -> Cons(x + 1, y) })

// 3.16
fun doubleToString(xs: List<Double>): List<String> =
    foldLeft(reverse(xs), List.empty(), { y, x -> Cons(x.toString(), y) })

fun doubleToString2(xs: List<Double>): List<String> =
    foldRight(xs, List.empty(), { x, y -> Cons(x.toString(), y) })

// 3.17
fun <A, B> map(xs: List<A>, f: (A) -> B): List<B> =
    foldLeft(reverse(xs), List.empty(), { y, x -> Cons(f(x), y) })

fun <A, B> map2(xs: List<A>, f: (A) -> B): List<B> =
    foldRight(xs, List.empty(), { x, y -> Cons(f(x), y) })

// 3.18
fun <A> filter(xs: List<A>, f: (A) -> Boolean): List<A> =
    foldLeft(reverse(xs), List.empty(), { y, x -> if (f(x)) Cons(x, y) else filter(y, f) })

fun <A> filter_2(xs: List<A>, f: (A) -> Boolean): List<A> =
    foldRight(xs, List.empty(), { x, y -> if (f(x)) Cons(x, y) else filter(y, f) })

// 3.19
fun <A, B> flatMap(xa: List<A>, f: (A) -> List<B>): List<B> =
    foldLeft(reverse(xa), List.empty(), { y, x -> append(f(x), y) })

fun <A, B> flatMap2(xa: List<A>, f: (A) -> List<B>): List<B> =
    foldRight(xa, List.empty(), { x, y -> append(f(x), y) })

// 3.20
fun <A> filter2(xa: List<A>, f: (A) -> Boolean): List<A> =
    flatMap(xa) { x -> if (f(x)) List.of(x) else List.empty() }

// 3.21
fun add(xa: List<Int>, xb: List<Int>): List<Int> =
    when (xa) {
        is Nil -> List.empty()
        is Cons -> when (xb) {
            is Nil -> List.empty()
            is Cons -> Cons(xa.head + xb.head, add(xa.tail, xb.tail))
        }
    }

// 3.22
fun <A> zipWith(xa: List<A>, xb: List<A>, f: (A, A) -> A): List<A> =
    when (xa) {
        is Nil -> List.empty()
        is Cons -> when (xb) {
            is Nil -> List.empty()
            is Cons -> Cons(f(xa.head, xb.head), zipWith(xa.tail, xb.tail, f))
        }
    }

// 3.23
tailrec fun <A> startsWith(target: List<A>, sub: List<A>): Boolean =
    when (target) {
        is Nil -> sub == Nil
        is Cons -> when (sub) {
            is Nil -> true
            is Cons ->
                if (target.head == sub.head) startsWith(target.tail, sub.tail)
                else false
        }
    }

tailrec fun <A> hasSubsequence(xs: List<A>, sub: List<A>): Boolean =
    when (xs) {
        is Nil -> false
        is Cons -> {
            if (startsWith(xs, sub)) true
            else hasSubsequence(xs.tail, sub)
        }
    }

// 3.24
fun <A> size(tree: Tree<A>): Int =
    when (tree) {
        is Leaf -> 1
        is Branch -> 1 + size(tree.left) + size(tree.right)
    }

// 3.25
fun maximum(tree: Tree<Int>): Int =
    when (tree) {
        is Leaf -> tree.value
        is Branch -> maxOf(maximum(tree.left), maximum(tree.right))
    }

// 3.26
fun depth(tree: Tree<Int>): Int =
    when (tree) {
        is Leaf -> 0
        is Branch -> maxOf(1 + depth(tree.left), 1 + depth(tree.right))
    }

// 3.27
fun <A, B> map(tree: Tree<A>, f: (A) -> B): Tree<B> =
    when (tree) {
        is Leaf -> Leaf(f(tree.value))
        is Branch -> Branch(map(tree.left, f), map(tree.right, f))
    }

// 3.28
fun <A, B> fold(ta: Tree<A>, l: (A) -> B, b: (B, B) -> B): B =
    when (ta) {
        is Leaf -> l(ta.value)
        is Branch -> b(fold(ta.left, l, b), fold(ta.right, l, b))
    }

fun <A> sizeF(ta: Tree<A>): Int =
    fold(ta, { 1 }, { b1, b2 -> 1 + b1 + b2 })

fun maximumF(ta: Tree<Int>): Int =
    fold(ta, { a -> a }, { b1, b2 -> maxOf(b1, b2) })

fun <A> depthF(ta: Tree<A>): Int =
    fold(ta, { 0 }, { b1, b2 -> maxOf(1 + b1, 1 + b2) })

fun <A, B> mapF(ta: Tree<A>, f: (A) -> B): Tree<B> =
    fold(ta, { a -> Leaf(f(a)) }, { b1: Tree<B>, b2: Tree<B> -> Branch(b1, b2) })
