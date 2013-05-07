package michael.gutin;
public class MinTree<T> {

	Node<T> root = null;
	Node<T> left = null;
	
	public MinTree(){}
	
	public T minData()
	{
		return left.data;
	}
	
	public float minValue()
	{
			return left.value;
	}
	
	
	public boolean isEmpty()
	{
		if(left == null)
			return true;
		return false;
	}
	/**
	 * Pops left node
	 */
	public void popMin()
	{
		if(left == null)
			return;
		if(left.right==null)
		{
			left = left.parent;
			if(left!=null)
				left.left = null;
			else
				root=null;
		}
		else
		{
			if(left.parent!=null)
				left.parent.left = left.right;
			left.right.parent = left.parent;
			if(left.parent==null)
				root=left;
			left = left.right;
			while(left.left!=null)
				left=left.left;
		}
	}
	
	
	
	public String toString()
	{
		String output = "";
		output+=toString(root);
		output=output.substring(0,output.length()-2);
		return output;
	}
	
	public String toString(Node<T> node)
	{
		String output = "";
		if(node.left !=null)
			output+=toString(node.left);
		output+= node.toString()+ ", ";
		if(node.right!=null)
			output+=toString(node.right);
		return output;
	}
	
	
	void remove(Node<T> target)
	{
		if(target == left)
			popMin();
		if(target.left== null && target.right == null)
		{
			if(!target.setParentsChildTo(null))
				root = null;
		}
		else if(target.left==null || target.right == null)
		{
				if(!target.setParentsChildTo(target.getOnlyChild()))
				{
						root = target.getOnlyChild();
						root.parent=null;
				}
				else
					target.getOnlyChild().parent = target.parent;
		}
		else
		{
			Node<T> leftMost = target.right;
			while(leftMost.left!=null)
				leftMost = leftMost.left;
			target.value = leftMost.value;
			target.data = leftMost.data;
			remove(leftMost);
		}
		
	}
	
	boolean endContains;
	
	boolean contains(Node<T> target)
	{
		endContains=false;
		return contains(target,root);
	}
	
	boolean contains(Node<T> target, Node<T> current)
	{
		if(endContains)
			return true;
		boolean output=false;
		
		if(target.data.equals(current.data))
		{
			if(target.value<current.value)
			{
				current.value = target.value;
				remove(current);
				add(new Node<T>(current.data,current.value),false);
			}
			endContains=true;
			return true;
		}
		
		if(current.left !=null)
			output = output || contains(target,current.left);
		if(current.right !=null)
			output = output || contains(target,current.right);
		return output;
	}
	
	private void add(Node<T> node,boolean check)
	{
		add(node.data,node.value,check);
	}
	
	public void add(T data, float value)
	{
		add(data,value,true);
	}
	
	private void add(T data, float index, boolean check)
	{
		Node<T> latest = new Node<T>(data,index);
		if(root==null)
		{
		   root = latest;
		   left = latest;
		   return;
		}
		if(check && !contains(latest))
			add(latest,root);
	}
	
	private void add(Node<T> latest, Node<T> current)
	{
		int cmp = compare(latest,current);//1 means latest is right child, -1 means latest is left child
		if(cmp==1)
			tryRight(latest,current);
		else if(cmp==-1)
			tryLeft(latest,current);
		else
		{
			if(current.left==null)
			{
				current.left = latest;
				current.left.parent = current;
				if(left==current)
					left=current.left;
			}
			else if(current.right==null)
			{
				current.right=latest;
				current.right.parent = current;
			}
			else
				add(latest,current.left);
		}
	}
	
	public void tryLeft(Node<T> latest, Node<T> current)
	{
		if(current.left==null)
		{
			current.left=latest;
			current.left.parent = current;
			if(left==current)
				left=current.left;
		}
		else
			add(latest,current.left);
	}
	
	public void tryRight(Node<T> latest, Node<T> current)
	{
		if(current.right==null)
		{
			current.right=latest;
			current.right.parent = current;
		}
		else
			add(latest,current.right);
	}
	
	public int compare(Node<T> left, Node<T> right)
	{
		if(left.value>right.value)
			return 1;
		else if (left.value<right.value)
			return -1;
		else
			return 0;
	}
	
	
	public class Node<S>{
		public S data;
		public float value;
		public Node<S> parent=null;
		public Node<S> left=null;
		public Node<S> right=null;
		
		public Node(S data, float value)
		{
			this.data = data;
			this.value = value;
		}
		
		public Node<S> copy()
		{
			Node<S> output = new Node<S>(data,value);
			return output;
		}
		
		public String toString()
		{
			return data.toString() +": " + value;
		}
		
		public Node<S> getOnlyChild()
		{
			if(left==null && right == null)
				return null;
			else if(left==null)
				return this.right;
			else if(right==null)
				return this.left;
			else
				return null;
		}
		
		/*
		 * return false if parent is null, this also means nothing is done
		 */
		public boolean setParentsChildTo(Node<S> target)
		{
			if(parent==null)
				return false;
			if(parent.left == this)
				parent.left = target;
			else if(parent.right == this)
				parent.right = target;
			return true;
		}
		
		
	}
}
