function c45_iris
    x = 1:.01:7;
    y = 0.1:.001:2.5;
    
    for a=1:numel(x)
        for b=1:numel(y)
            j(a,b)=classifica(x(a),y(b));
        end
    end  
    imshow(j,[]);
    colormap(jet);
end


function id=classifica(pl,pw)
id=0;
if pw<=0.6 
    id=1;
    return;
end
if pw>1.7 
    id=2;
    return;
end
if pl<=4.9 
    id=3;
    return;
end
if pw>1.5 
    id=3;
    return;
end

    id=2;

end